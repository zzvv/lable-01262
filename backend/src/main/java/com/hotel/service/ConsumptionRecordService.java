package com.hotel.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.common.Constants;
import com.hotel.common.BusinessException;
import com.hotel.entity.ConsumptionRecord;
import com.hotel.entity.CheckIn;
import com.hotel.mapper.ConsumptionRecordMapper;
import com.hotel.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 消费记录服务
 */
@Service
@RequiredArgsConstructor
public class ConsumptionRecordService extends ServiceImpl<ConsumptionRecordMapper, ConsumptionRecord> {

    private final CheckInService checkInService;

    /**
     * 分页查询消费记录
     */
    public IPage<ConsumptionRecord> page(Page<ConsumptionRecord> page, Long checkInId, String itemName,
                                          Integer paymentStatus, LocalDateTime startTime, LocalDateTime endTime) {
        return lambdaQuery()
                .eq(checkInId != null, ConsumptionRecord::getCheckInId, checkInId)
                .like(itemName != null, ConsumptionRecord::getItemName, itemName)
                .eq(paymentStatus != null, ConsumptionRecord::getPaymentStatus, paymentStatus)
                .between(startTime != null && endTime != null, ConsumptionRecord::getCreateTime, startTime, endTime)
                .eq(ConsumptionRecord::getDeleted, 0)
                .orderByDesc(ConsumptionRecord::getCreateTime)
                .page(page);
    }

    /**
     * 根据入住ID查询消费记录列表
     */
    public List<ConsumptionRecord> getByCheckInId(Long checkInId) {
        return lambdaQuery()
                .eq(ConsumptionRecord::getCheckInId, checkInId)
                .eq(ConsumptionRecord::getDeleted, 0)
                .orderByDesc(ConsumptionRecord::getCreateTime)
                .list();
    }

    /**
     * 创建消费记录
     */
    @Transactional(rollbackFor = Exception.class)
    public ConsumptionRecord createConsumption(ConsumptionRecord record) {
        // 验证金额
        validateAmount(record.getAmount());
        validateAmount(record.getUnitPrice());

        // 检查入住记录是否存在且未退房
        CheckIn checkIn = checkInService.getById(record.getCheckInId());
        if (checkIn == null) {
            throw new BusinessException("入住记录不存在");
        }
        if (checkIn.getStatus() == Constants.CheckInStatus.CHECKED_OUT) {
            throw new BusinessException("该入住记录已退房，无法添加消费");
        }

        // 计算金额（如果没提供）
        if (record.getAmount() == null && record.getQuantity() != null && record.getUnitPrice() != null) {
            record.setAmount(record.getUnitPrice().multiply(BigDecimal.valueOf(record.getQuantity())));
        }

        record.setOperatorId(SecurityUtils.getUserId());
        record.setStatus(1); // 正常状态
        record.setDeleted(0);
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());

        save(record);
        return record;
    }

    /**
     * 更新消费记录
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateConsumption(ConsumptionRecord record) {
        // 验证金额
        if (record.getAmount() != null) {
            validateAmount(record.getAmount());
        }
        if (record.getUnitPrice() != null) {
            validateAmount(record.getUnitPrice());
        }

        ConsumptionRecord existing = getById(record.getId());
        if (existing == null) {
            throw new BusinessException("消费记录不存在");
        }

        // 检查入住记录是否已退房
        CheckIn checkIn = checkInService.getById(existing.getCheckInId());
        if (checkIn != null && checkIn.getStatus() == Constants.CheckInStatus.CHECKED_OUT) {
            throw new BusinessException("该入住记录已退房，无法修改消费记录");
        }

        record.setUpdateTime(LocalDateTime.now());
        updateById(record);
    }

    /**
     * 删除消费记录
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteConsumption(Long id) {
        ConsumptionRecord record = getById(id);
        if (record == null) {
            throw new BusinessException("消费记录不存在");
        }

        // 检查入住记录是否已退房
        CheckIn checkIn = checkInService.getById(record.getCheckInId());
        if (checkIn != null && checkIn.getStatus() == Constants.CheckInStatus.CHECKED_OUT) {
            throw new BusinessException("该入住记录已退房，无法删除消费记录");
        }

        lambdaUpdate()
                .set(ConsumptionRecord::getDeleted, 1)
                .eq(ConsumptionRecord::getId, id)
                .update();
    }

    /**
     * 作废消费记录
     */
    @Transactional(rollbackFor = Exception.class)
    public void cancelConsumption(Long id) {
        ConsumptionRecord record = getById(id);
        if (record == null) {
            throw new BusinessException("消费记录不存在");
        }

        // 检查是否已经作废
        if (record.getStatus() == 0) {
            throw new BusinessException("该消费记录已经作废");
        }

        lambdaUpdate()
                .set(ConsumptionRecord::getStatus, 0)
                .set(ConsumptionRecord::getCancelledBy, SecurityUtils.getUserId())
                .set(ConsumptionRecord::getCancelledTime, LocalDateTime.now())
                .eq(ConsumptionRecord::getId, id)
                .update();
    }

    /**
     * 支付消费记录
     */
    @Transactional(rollbackFor = Exception.class)
    public void payConsumption(Long id, Integer paymentMethod) {
        ConsumptionRecord record = getById(id);
        if (record == null) {
            throw new BusinessException("消费记录不存在");
        }

        if (record.getPaymentStatus() == Constants.PaymentStatus.PAID) {
            throw new BusinessException("该消费记录已经支付");
        }

        lambdaUpdate()
                .set(ConsumptionRecord::getPaymentMethod, paymentMethod)
                .set(ConsumptionRecord::getPaymentStatus, Constants.PaymentStatus.PAID)
                .set(ConsumptionRecord::getPaymentTime, LocalDateTime.now())
                .eq(ConsumptionRecord::getId, id)
                .update();
    }

    /**
     * 验证金额格式
     */
    private void validateAmount(BigDecimal amount) {
        if (amount == null) {
            return;
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("金额必须大于0");
        }
        if (amount.scale() > 2) {
            throw new BusinessException("金额最多保留两位小数");
        }
    }
}
