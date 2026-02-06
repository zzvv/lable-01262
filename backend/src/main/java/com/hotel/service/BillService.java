package com.hotel.service;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.common.Constants;
import com.hotel.dto.BillExportVO;
import com.hotel.entity.Bill;
import com.hotel.mapper.BillMapper;
import com.hotel.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 账单服务
 */
@Service
@RequiredArgsConstructor
public class BillService extends ServiceImpl<BillMapper, Bill> {

    private static final String[] BILL_TYPE_NAMES = {"房费", "消费", "押金", "退款"};
    private static final String[] PAYMENT_METHOD_NAMES = {"现金", "微信", "支付宝", "银行卡", "挂账"};
    private static final String[] PAYMENT_STATUS_NAMES = {"未支付", "已支付", "已退款"};

    /**
     * 分页查询账单
     */
    public IPage<Bill> page(Page<Bill> page, String billNo, String customerName,
                            Integer billType, Integer paymentStatus, LocalDate startDate, LocalDate endDate) {
        return baseMapper.selectBillPage(page, billNo, customerName, billType, paymentStatus, startDate, endDate);
    }

    /**
     * 导出账单列表
     */
    public List<BillExportVO> listForExport(String billNo, String customerName,
                                             Integer billType, Integer paymentStatus,
                                             LocalDate startDate, LocalDate endDate) {
        List<Bill> bills = baseMapper.selectBillList(billNo, customerName, billType, paymentStatus, startDate, endDate);
        return bills.stream().map(this::convertToExportVO).toList();
    }

    private BillExportVO convertToExportVO(Bill bill) {
        BillExportVO vo = new BillExportVO();
        vo.setBillNo(bill.getBillNo());
        vo.setCustomerName(bill.getCustomer() != null ? bill.getCustomer().getName() : "");
        vo.setRoomNumber(bill.getCheckIn() != null && bill.getCheckIn().getRoom() != null 
                         ? bill.getCheckIn().getRoom().getRoomNumber() : "");
        vo.setBillTypeName(bill.getBillType() != null && bill.getBillType() < BILL_TYPE_NAMES.length 
                           ? BILL_TYPE_NAMES[bill.getBillType()] : "");
        vo.setItemName(bill.getItemName());
        vo.setAmount(bill.getAmount());
        vo.setPaymentMethodName(bill.getPaymentMethod() != null && bill.getPaymentMethod() < PAYMENT_METHOD_NAMES.length 
                                ? PAYMENT_METHOD_NAMES[bill.getPaymentMethod()] : "");
        vo.setPaymentStatusName(bill.getPaymentStatus() != null && bill.getPaymentStatus() < PAYMENT_STATUS_NAMES.length 
                                ? PAYMENT_STATUS_NAMES[bill.getPaymentStatus()] : "");
        vo.setPaymentTime(bill.getPaymentTime());
        vo.setCreateTime(bill.getCreateTime());
        vo.setRemark(bill.getRemark());
        return vo;
    }

    /**
     * 根据入住ID查询账单列表
     */
    public List<Bill> getByCheckInId(Long checkInId) {
        return baseMapper.selectByCheckInId(checkInId);
    }

    /**
     * 创建账单
     */
    public Bill createBill(Long checkInId, Long customerId, Integer billType, String itemName, BigDecimal amount) {
        Bill bill = new Bill();
        bill.setBillNo("BL" + IdUtil.getSnowflakeNextIdStr());
        bill.setCheckInId(checkInId);
        bill.setCustomerId(customerId);
        bill.setBillType(billType);
        bill.setItemName(itemName);
        bill.setAmount(amount);
        bill.setPaymentStatus(Constants.PaymentStatus.UNPAID);
        bill.setOperatorId(SecurityUtils.getUserId());
        save(bill);
        return bill;
    }

    /**
     * 创建账单并支付
     */
    public Bill createBillAndPay(Long checkInId, Long customerId, Integer billType,
                                  String itemName, BigDecimal amount, Integer paymentMethod) {
        Bill bill = new Bill();
        bill.setBillNo("BL" + IdUtil.getSnowflakeNextIdStr());
        bill.setCheckInId(checkInId);
        bill.setCustomerId(customerId);
        bill.setBillType(billType);
        bill.setItemName(itemName);
        bill.setAmount(amount);
        bill.setPaymentMethod(paymentMethod);
        bill.setPaymentStatus(Constants.PaymentStatus.PAID);
        bill.setPaymentTime(LocalDateTime.now());
        bill.setOperatorId(SecurityUtils.getUserId());
        save(bill);
        return bill;
    }

    /**
     * 支付账单
     */
    public void payBill(Long id, Integer paymentMethod) {
        Bill bill = new Bill();
        bill.setId(id);
        bill.setPaymentMethod(paymentMethod);
        bill.setPaymentStatus(Constants.PaymentStatus.PAID);
        bill.setPaymentTime(LocalDateTime.now());
        updateById(bill);
    }

    /**
     * 统计今日营收
     */
    public BigDecimal sumTodayRevenue() {
        return baseMapper.sumTodayRevenue(LocalDate.now());
    }

    /**
     * 统计本月营收
     */
    public BigDecimal sumMonthRevenue() {
        LocalDate now = LocalDate.now();
        return baseMapper.sumMonthRevenue(now.getYear(), now.getMonthValue());
    }

    /**
     * 按账单类型统计金额
     */
    public List<Map<String, Object>> sumByBillType() {
        return baseMapper.sumByBillType();
    }
}
