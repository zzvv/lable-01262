package com.hotel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotel.entity.Bill;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 账单Mapper
 */
@Mapper
public interface BillMapper extends BaseMapper<Bill> {

    /**
     * 分页查询账单（带关联信息）
     */
    IPage<Bill> selectBillPage(Page<Bill> page,
                                @Param("billNo") String billNo,
                                @Param("customerName") String customerName,
                                @Param("billType") Integer billType,
                                @Param("paymentStatus") Integer paymentStatus,
                                @Param("startDate") LocalDate startDate,
                                @Param("endDate") LocalDate endDate);

    /**
     * 查询账单列表（用于导出）
     */
    List<Bill> selectBillList(@Param("billNo") String billNo,
                               @Param("customerName") String customerName,
                               @Param("billType") Integer billType,
                               @Param("paymentStatus") Integer paymentStatus,
                               @Param("startDate") LocalDate startDate,
                               @Param("endDate") LocalDate endDate);

    /**
     * 根据入住ID查询账单列表
     */
    @Select("SELECT * FROM bill WHERE check_in_id = #{checkInId} AND deleted = 0 ORDER BY create_time")
    List<Bill> selectByCheckInId(@Param("checkInId") Long checkInId);

    /**
     * 统计今日营收
     */
    @Select("SELECT COALESCE(SUM(amount), 0) FROM bill WHERE DATE(payment_time) = #{date} " +
            "AND payment_status = 1 AND bill_type != 3 AND deleted = 0")
    BigDecimal sumTodayRevenue(@Param("date") LocalDate date);

    /**
     * 统计本月营收
     */
    @Select("SELECT COALESCE(SUM(amount), 0) FROM bill WHERE YEAR(payment_time) = #{year} " +
            "AND MONTH(payment_time) = #{month} AND payment_status = 1 AND bill_type != 3 AND deleted = 0")
    BigDecimal sumMonthRevenue(@Param("year") Integer year, @Param("month") Integer month);

    /**
     * 按账单类型统计金额
     */
    @Select("SELECT bill_type, SUM(amount) as amount FROM bill " +
            "WHERE payment_status = 1 AND deleted = 0 GROUP BY bill_type")
    List<Map<String, Object>> sumByBillType();

    /**
     * 按类型和日期统计金额
     */
    @Select("SELECT COALESCE(SUM(amount), 0) FROM bill WHERE bill_type = #{billType} " +
            "AND DATE(payment_time) = #{date} AND payment_status = 1 AND deleted = 0")
    BigDecimal sumByTypeAndDate(@Param("billType") Integer billType, @Param("date") LocalDate date);

    /**
     * 按类型和月份统计金额
     */
    @Select("SELECT COALESCE(SUM(amount), 0) FROM bill WHERE bill_type = #{billType} " +
            "AND YEAR(payment_time) = #{year} AND MONTH(payment_time) = #{month} " +
            "AND payment_status = 1 AND deleted = 0")
    BigDecimal sumByTypeAndMonth(@Param("billType") Integer billType, @Param("year") Integer year, @Param("month") Integer month);
}
