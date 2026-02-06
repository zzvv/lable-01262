package com.hotel.service;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.common.BusinessException;
import com.hotel.common.Constants;
import com.hotel.entity.Customer;
import com.hotel.entity.Reservation;
import com.hotel.entity.Room;
import com.hotel.entity.RoomType;
import com.hotel.mapper.ReservationMapper;
import com.hotel.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

/**
 * 预订服务
 */
@Service
@RequiredArgsConstructor
public class ReservationService extends ServiceImpl<ReservationMapper, Reservation> {

    private final CustomerService customerService;
    private final RoomTypeService roomTypeService;
    private final RoomService roomService;

    /**
     * 分页查询预订
     */
    public IPage<Reservation> page(Page<Reservation> page, String reservationNo, String customerName,
                                    String phone, Integer status, LocalDate startDate, LocalDate endDate) {
        return baseMapper.selectReservationPage(page, reservationNo, customerName, phone, status, startDate, endDate);
    }

    /**
     * 根据ID查询预订详情
     */
    public Reservation getDetailById(Long id) {
        return baseMapper.selectReservationById(id);
    }

    /**
     * 创建预订
     */
    @Transactional(rollbackFor = Exception.class)
    public Reservation createReservation(Reservation reservation, Customer customer) {
        // 获取或创建客户
        Customer savedCustomer = customerService.getOrCreate(customer);
        reservation.setCustomerId(savedCustomer.getId());

        // 验证房型
        RoomType roomType = roomTypeService.getById(reservation.getRoomTypeId());
        if (roomType == null || roomType.getStatus() != 1) {
            throw new BusinessException("房型不存在或已下架");
        }

        // 计算入住天数
        long nights = ChronoUnit.DAYS.between(reservation.getCheckInDate(), reservation.getCheckOutDate());
        if (nights <= 0) {
            throw new BusinessException("退房日期必须晚于入住日期");
        }
        reservation.setNights((int) nights);

        // 计算总价
        BigDecimal totalPrice = roomType.getPrice().multiply(BigDecimal.valueOf(nights))
                .multiply(BigDecimal.valueOf(reservation.getRoomCount()));
        reservation.setTotalPrice(totalPrice);

        // 生成预订单号
        reservation.setReservationNo("RV" + IdUtil.getSnowflakeNextIdStr());
        reservation.setStatus(Constants.ReservationStatus.PENDING);
        reservation.setOperatorId(SecurityUtils.getUserId());

        save(reservation);
        return reservation;
    }

    /**
     * 确认预订
     */
    @Transactional(rollbackFor = Exception.class)
    public void confirmReservation(Long id) {
        Reservation reservation = getById(id);
        if (reservation == null) {
            throw new BusinessException("预订不存在");
        }
        if (reservation.getStatus() != Constants.ReservationStatus.PENDING) {
            throw new BusinessException("只能确认待确认的预订");
        }

        // 分配房间
        Room room = roomService.assignRoom(reservation.getRoomTypeId());
        reservation.setRoomId(room.getId());
        reservation.setStatus(Constants.ReservationStatus.CONFIRMED);
        updateById(reservation);
    }

    /**
     * 取消预订
     */
    @Transactional(rollbackFor = Exception.class)
    public void cancelReservation(Long id, String reason) {
        Reservation reservation = getById(id);
        if (reservation == null) {
            throw new BusinessException("预订不存在");
        }
        if (reservation.getStatus() >= Constants.ReservationStatus.CHECKED_IN) {
            throw new BusinessException("已入住或已完成的预订不能取消");
        }

        // 如果已分配房间，释放房间
        if (reservation.getRoomId() != null) {
            roomService.updateStatus(reservation.getRoomId(), Constants.RoomStatus.FREE);
        }

        reservation.setStatus(Constants.ReservationStatus.CANCELLED);
        reservation.setCancelReason(reason);
        updateById(reservation);
    }

    /**
     * 查询今日预计入住数
     */
    public Integer countTodayCheckIn() {
        return baseMapper.countTodayCheckIn(LocalDate.now());
    }

    /**
     * 查询今日预计离店数
     */
    public Integer countTodayCheckOut() {
        return baseMapper.countTodayCheckOut(LocalDate.now());
    }

    /**
     * 统计各状态预订数量
     */
    public List<Map<String, Object>> countByStatus() {
        return baseMapper.countByStatus();
    }
}
