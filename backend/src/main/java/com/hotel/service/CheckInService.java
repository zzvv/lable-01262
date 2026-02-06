package com.hotel.service;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.common.BusinessException;
import com.hotel.common.Constants;
import com.hotel.entity.*;
import com.hotel.mapper.CheckInMapper;
import com.hotel.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

/**
 * 入住服务
 */
@Service
@RequiredArgsConstructor
public class CheckInService extends ServiceImpl<CheckInMapper, CheckIn> {

    private final ReservationService reservationService;
    private final CustomerService customerService;
    private final RoomService roomService;
    private final RoomTypeService roomTypeService;
    private final BillService billService;

    /**
     * 分页查询入住记录
     */
    public IPage<CheckIn> page(Page<CheckIn> page, String checkInNo, String customerName,
                                String roomNumber, Integer status, LocalDate startDate, LocalDate endDate) {
        return baseMapper.selectCheckInPage(page, checkInNo, customerName, roomNumber, status, startDate, endDate);
    }

    /**
     * 根据ID查询入住详情
     */
    public CheckIn getDetailById(Long id) {
        return baseMapper.selectCheckInById(id);
    }

    /**
     * 预订转入住
     */
    @Transactional(rollbackFor = Exception.class)
    public CheckIn checkInFromReservation(Long reservationId, BigDecimal deposit) {
        Reservation reservation = reservationService.getById(reservationId);
        if (reservation == null) {
            throw new BusinessException("预订不存在");
        }
        if (reservation.getStatus() != Constants.ReservationStatus.CONFIRMED) {
            throw new BusinessException("只能办理已确认的预订");
        }

        // 创建入住记录
        CheckIn checkIn = new CheckIn();
        checkIn.setCheckInNo("CI" + IdUtil.getSnowflakeNextIdStr());
        checkIn.setReservationId(reservationId);
        checkIn.setCustomerId(reservation.getCustomerId());
        checkIn.setRoomId(reservation.getRoomId());
        checkIn.setCheckInTime(LocalDateTime.now());
        checkIn.setExpectedCheckOut(reservation.getCheckOutDate());
        checkIn.setNights(reservation.getNights());

        // 获取房型价格
        RoomType roomType = roomTypeService.getById(reservation.getRoomTypeId());
        checkIn.setRoomPrice(roomType.getPrice());
        checkIn.setTotalPrice(reservation.getTotalPrice());
        checkIn.setDeposit(deposit != null ? deposit : BigDecimal.ZERO);
        checkIn.setExtraCharges(BigDecimal.ZERO);
        checkIn.setDiscount(BigDecimal.ZERO);
        checkIn.setActualAmount(reservation.getTotalPrice());
        checkIn.setPaidAmount(deposit != null ? deposit : BigDecimal.ZERO);
        checkIn.setStatus(Constants.CheckInStatus.IN_STAY);
        checkIn.setOperatorId(SecurityUtils.getUserId());

        save(checkIn);

        // 更新预订状态
        reservation.setStatus(Constants.ReservationStatus.CHECKED_IN);
        reservationService.updateById(reservation);

        // 更新房间状态
        roomService.updateStatus(reservation.getRoomId(), Constants.RoomStatus.OCCUPIED);

        // 创建押金账单
        if (deposit != null && deposit.compareTo(BigDecimal.ZERO) > 0) {
            billService.createBill(checkIn.getId(), reservation.getCustomerId(),
                    Constants.BillType.DEPOSIT, "押金", deposit);
        }

        return checkIn;
    }

    /**
     * 散客入住
     */
    @Transactional(rollbackFor = Exception.class)
    public CheckIn walkInCheckIn(CheckIn checkIn, Customer customer, Long roomId, BigDecimal deposit) {
        // 获取或创建客户
        Customer savedCustomer = customerService.getOrCreate(customer);
        checkIn.setCustomerId(savedCustomer.getId());

        // 验证房间
        Room room = roomService.getDetailById(roomId);
        if (room == null) {
            throw new BusinessException("房间不存在");
        }
        if (room.getStatus() != Constants.RoomStatus.FREE) {
            throw new BusinessException("房间不可用");
        }

        // 计算入住天数
        long nights = ChronoUnit.DAYS.between(LocalDate.now(), checkIn.getExpectedCheckOut());
        if (nights <= 0) {
            throw new BusinessException("退房日期必须晚于今天");
        }

        // 获取房型价格
        RoomType roomType = roomTypeService.getById(room.getRoomTypeId());

        checkIn.setCheckInNo("CI" + IdUtil.getSnowflakeNextIdStr());
        checkIn.setRoomId(roomId);
        checkIn.setCheckInTime(LocalDateTime.now());
        checkIn.setNights((int) nights);
        checkIn.setRoomPrice(roomType.getPrice());
        checkIn.setTotalPrice(roomType.getPrice().multiply(BigDecimal.valueOf(nights)));
        checkIn.setDeposit(deposit != null ? deposit : BigDecimal.ZERO);
        checkIn.setExtraCharges(BigDecimal.ZERO);
        checkIn.setDiscount(BigDecimal.ZERO);
        checkIn.setActualAmount(checkIn.getTotalPrice());
        checkIn.setPaidAmount(deposit != null ? deposit : BigDecimal.ZERO);
        checkIn.setStatus(Constants.CheckInStatus.IN_STAY);
        checkIn.setOperatorId(SecurityUtils.getUserId());

        save(checkIn);

        // 更新房间状态
        roomService.updateStatus(roomId, Constants.RoomStatus.OCCUPIED);

        // 创建押金账单
        if (deposit != null && deposit.compareTo(BigDecimal.ZERO) > 0) {
            billService.createBill(checkIn.getId(), savedCustomer.getId(),
                    Constants.BillType.DEPOSIT, "押金", deposit);
        }

        return checkIn;
    }

    /**
     * 退房结算
     */
    @Transactional(rollbackFor = Exception.class)
    public CheckIn checkOut(Long id, BigDecimal extraCharges, BigDecimal discount, Integer paymentMethod) {
        CheckIn checkIn = getById(id);
        if (checkIn == null) {
            throw new BusinessException("入住记录不存在");
        }
        if (checkIn.getStatus() != Constants.CheckInStatus.IN_STAY) {
            throw new BusinessException("该记录不是入住中状态");
        }

        // 计算实际入住天数
        long actualNights = ChronoUnit.DAYS.between(
                checkIn.getCheckInTime().toLocalDate(), LocalDate.now());
        if (actualNights < 1) actualNights = 1;

        // 重新计算房费
        BigDecimal roomFee = checkIn.getRoomPrice().multiply(BigDecimal.valueOf(actualNights));

        // 计算总费用
        BigDecimal totalAmount = roomFee
                .add(extraCharges != null ? extraCharges : BigDecimal.ZERO)
                .subtract(discount != null ? discount : BigDecimal.ZERO);

        // 计算应付金额（扣除已付押金）
        BigDecimal shouldPay = totalAmount.subtract(checkIn.getPaidAmount());

        checkIn.setCheckOutTime(LocalDateTime.now());
        checkIn.setNights((int) actualNights);
        checkIn.setTotalPrice(roomFee);
        checkIn.setExtraCharges(extraCharges != null ? extraCharges : BigDecimal.ZERO);
        checkIn.setDiscount(discount != null ? discount : BigDecimal.ZERO);
        checkIn.setActualAmount(totalAmount);
        checkIn.setStatus(Constants.CheckInStatus.CHECKED_OUT);

        updateById(checkIn);

        // 创建房费账单
        billService.createBillAndPay(checkIn.getId(), checkIn.getCustomerId(),
                Constants.BillType.ROOM_FEE, "房费", roomFee, paymentMethod);

        // 创建额外消费账单
        if (extraCharges != null && extraCharges.compareTo(BigDecimal.ZERO) > 0) {
            billService.createBillAndPay(checkIn.getId(), checkIn.getCustomerId(),
                    Constants.BillType.CONSUMPTION, "额外消费", extraCharges, paymentMethod);
        }

        // 更新房间状态为清洁中
        roomService.updateStatus(checkIn.getRoomId(), Constants.RoomStatus.CLEANING);

        // 更新预订状态
        if (checkIn.getReservationId() != null) {
            Reservation reservation = reservationService.getById(checkIn.getReservationId());
            reservation.setStatus(Constants.ReservationStatus.COMPLETED);
            reservationService.updateById(reservation);
        }

        // 更新客户消费信息
        customerService.updateConsumption(checkIn.getCustomerId(), totalAmount);

        return checkIn;
    }

    /**
     * 查询当前在住人数
     */
    public Integer countCurrentGuests() {
        return baseMapper.countCurrentGuests();
    }

    /**
     * 统计每日入住数量
     */
    public List<Map<String, Object>> countDailyCheckIn() {
        return baseMapper.countDailyCheckIn();
    }

    /**
     * 统计每日营收
     */
    public List<Map<String, Object>> countDailyRevenue() {
        return baseMapper.countDailyRevenue();
    }
}
