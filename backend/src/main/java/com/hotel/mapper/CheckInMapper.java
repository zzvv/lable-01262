package com.hotel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotel.entity.CheckIn;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 入住记录Mapper
 */
@Mapper
public interface CheckInMapper extends BaseMapper<CheckIn> {

    /**
     * 分页查询入住记录（带关联信息）
     */
    IPage<CheckIn> selectCheckInPage(Page<CheckIn> page,
                                      @Param("checkInNo") String checkInNo,
                                      @Param("customerName") String customerName,
                                      @Param("roomNumber") String roomNumber,
                                      @Param("status") Integer status,
                                      @Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);

    /**
     * 根据ID查询入住记录（带关联信息）
     */
    CheckIn selectCheckInById(@Param("id") Long id);

    /**
     * 查询当前在住人数
     */
    @Select("SELECT COUNT(*) FROM check_in WHERE status = 0 AND deleted = 0")
    Integer countCurrentGuests();

    /**
     * 统计每日入住数量（近30天）
     */
    @Select("SELECT DATE(check_in_time) as date, COUNT(*) as count FROM check_in " +
            "WHERE check_in_time >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) AND deleted = 0 " +
            "GROUP BY DATE(check_in_time) ORDER BY date")
    List<Map<String, Object>> countDailyCheckIn();

    /**
     * 统计每日营收（近30天）
     */
    @Select("SELECT DATE(check_out_time) as date, SUM(actual_amount) as amount FROM check_in " +
            "WHERE check_out_time >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) AND status = 1 AND deleted = 0 " +
            "GROUP BY DATE(check_out_time) ORDER BY date")
    List<Map<String, Object>> countDailyRevenue();

    /**
     * 统计指定日期入住数
     */
    @Select("SELECT COUNT(*) FROM check_in WHERE DATE(check_in_time) = #{date} AND deleted = 0")
    Integer countByDate(@Param("date") LocalDate date);

    /**
     * 统计指定日期退房数
     */
    @Select("SELECT COUNT(*) FROM check_in WHERE DATE(check_out_time) = #{date} AND status = 1 AND deleted = 0")
    Integer countCheckOutByDate(@Param("date") LocalDate date);
}
