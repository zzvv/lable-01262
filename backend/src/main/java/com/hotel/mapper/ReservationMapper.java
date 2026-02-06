package com.hotel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotel.entity.Reservation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 预订Mapper
 */
@Mapper
public interface ReservationMapper extends BaseMapper<Reservation> {

    /**
     * 分页查询预订（带关联信息）
     */
    IPage<Reservation> selectReservationPage(Page<Reservation> page,
                                              @Param("reservationNo") String reservationNo,
                                              @Param("customerName") String customerName,
                                              @Param("phone") String phone,
                                              @Param("status") Integer status,
                                              @Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);

    /**
     * 根据ID查询预订（带关联信息）
     */
    Reservation selectReservationById(@Param("id") Long id);

    /**
     * 查询今日预计入住
     */
    @Select("SELECT COUNT(*) FROM reservation WHERE check_in_date = #{date} AND status IN (0, 1) AND deleted = 0")
    Integer countTodayCheckIn(@Param("date") LocalDate date);

    /**
     * 查询今日预计离店
     */
    @Select("SELECT COUNT(*) FROM reservation WHERE check_out_date = #{date} AND status = 2 AND deleted = 0")
    Integer countTodayCheckOut(@Param("date") LocalDate date);

    /**
     * 统计各状态预订数量
     */
    @Select("SELECT status, COUNT(*) as count FROM reservation WHERE deleted = 0 GROUP BY status")
    List<Map<String, Object>> countByStatus();

    /**
     * 统计指定日期预订数
     */
    @Select("SELECT COUNT(*) FROM reservation WHERE DATE(create_time) = #{date} AND deleted = 0")
    Integer countByDate(@Param("date") LocalDate date);
}
