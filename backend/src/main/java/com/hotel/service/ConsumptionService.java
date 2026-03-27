package com.hotel.service;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.common.BusinessException;
import com.hotel.common.Constants;
import com.hotel.dto.ConsumptionRequest;
import com.hotel.entity.CheckIn;
import com.hotel.entity.Consumption;
import com.hotel.entity.Customer;
import com.hotel.entity.Room;
import com.hotel.mapper.CheckInMapper;
import com.hotel.mapper.ConsumptionMapper;
import com.hotel.mapper.CustomerMapper;
import com.hotel.mapper.RoomMapper;
import com.hotel.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsumptionService extends ServiceImpl<ConsumptionMapper, Consumption> {

    private final CheckInMapper checkInMapper;
    private final CustomerMapper customerMapper;
    private final RoomMapper roomMapper;
    private final BillService billService;

    public IPage<Consumption> page(Page<Consumption> page, Long checkInId, String roomNumber,
                                    Integer status, Integer isBilled, LocalDateTime startDate, LocalDateTime endDate) {
        Page<Consumption> resultPage = baseMapper.selectConsumptionPage(page, checkInId, roomNumber, status, isBilled, startDate, endDate);
        
        resultPage.getRecords().forEach(this::fillExtraInfo);
        return resultPage;
    }

    public List<Consumption> getByCheckInId(Long checkInId) {
        List<Consumption> list = baseMapper.selectList(
            new LambdaQueryWrapper<Consumption>()
                .eq(Consumption::getCheckInId, checkInId)
                .orderByDesc(Consumption::getCreateTime)
        );
        list.forEach(this::fillExtraInfo);
        return list;
    }

    public Consumption getDetailById(Long id) {
        Consumption consumption = getById(id);
        if (consumption != null) {
            fillExtraInfo(consumption);
        }
        return consumption;
    }

    private void fillExtraInfo(Consumption consumption) {
        if (consumption.getCustomerId() != null) {
            Customer customer = customerMapper.selectById(consumption.getCustomerId());
            consumption.setCustomer(customer);
        }
        if (consumption.getCheckInId() != null) {
            CheckIn checkIn = checkInMapper.selectById(consumption.getCheckInId());
            consumption.setCheckIn(checkIn);
            if (checkIn != null && checkIn.getRoomId() != null) {
                Room room = roomMapper.selectById(checkIn.getRoomId());
                consumption.setRoomNumber(room != null ? room.getRoomNumber() : null);
            }
        }
    }

    @Transactional
    public Consumption createConsumption(ConsumptionRequest request) {
        CheckIn checkIn = checkInMapper.selectById(request.getCheckInId());
        if (checkIn == null) {
            throw new BusinessException("入住记录不存在");
        }
        if (checkIn.getStatus() == Constants.CheckInStatus.CHECKED_OUT) {
            throw new BusinessException("已退房的入住单不能添加消费");
        }

        BigDecimal amount = request.getAmount().setScale(2, RoundingMode.HALF_UP);
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("消费金额必须大于0");
        }

        Consumption consumption = new Consumption();
        consumption.setConsumptionNo("CS" + IdUtil.getSnowflakeNextIdStr());
        consumption.setCheckInId(request.getCheckInId());
        consumption.setCustomerId(checkIn.getCustomerId());
        consumption.setItemName(request.getItemName());
        consumption.setQuantity(request.getQuantity() != null ? request.getQuantity() : 1);
        consumption.setUnitPrice(request.getUnitPrice().setScale(2, RoundingMode.HALF_UP));
        consumption.setAmount(amount);
        consumption.setStatus(Constants.ConsumptionStatus.VALID);
        consumption.setOperatorId(SecurityUtils.getUserId());
        consumption.setRemark(request.getRemark());
        save(consumption);

        return consumption;
    }

    @Transactional
    public void cancelConsumption(Long id, String cancelRemark) {
        Consumption consumption = getById(id);
        if (consumption == null) {
            throw new BusinessException("消费记录不存在");
        }
        if (consumption.getStatus() == Constants.ConsumptionStatus.CANCELLED) {
            throw new BusinessException("该消费记录已作废");
        }

        CheckIn checkIn = checkInMapper.selectById(consumption.getCheckInId());
        if (checkIn != null && checkIn.getStatus() == Constants.CheckInStatus.CHECKED_OUT) {
            throw new BusinessException("已退房的消费记录不能作废");
        }

        consumption.setStatus(Constants.ConsumptionStatus.CANCELLED);
        consumption.setCancelOperatorId(SecurityUtils.getUserId());
        consumption.setCancelTime(LocalDateTime.now());
        consumption.setCancelRemark(cancelRemark);
        updateById(consumption);
    }

    @Transactional
    public void settleConsumptionToBill(Long checkInId, Integer paymentMethod) {
        CheckIn checkIn = checkInMapper.selectById(checkInId);
        if (checkIn == null) {
            throw new BusinessException("入住记录不存在");
        }

        List<Consumption> consumptions = baseMapper.selectList(
            new LambdaQueryWrapper<Consumption>()
                .eq(Consumption::getCheckInId, checkInId)
                .eq(Consumption::getStatus, Constants.ConsumptionStatus.VALID)
                .eq(Consumption::getIsBilled, 0)
        );

        for (Consumption consumption : consumptions) {
            var bill = billService.createBillAndPay(
                checkInId,
                checkIn.getCustomerId(),
                Constants.BillType.CONSUMPTION,
                consumption.getItemName(),
                consumption.getAmount(),
                paymentMethod
            );
            
            consumption.setIsBilled(1);
            consumption.setBillId(bill.getId());
            updateById(consumption);
        }
    }
}
