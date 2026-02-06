package com.hotel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 月报表实体
 */
@Data
@TableName("monthly_report")
public class MonthlyReport implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 年份 */
    private Integer reportYear;

    /** 月份 */
    private Integer reportMonth;

    /** 总营收 */
    private BigDecimal totalRevenue;

    /** 房费收入 */
    private BigDecimal roomRevenue;

    /** 消费收入 */
    private BigDecimal consumeRevenue;

    /** 押金收入 */
    private BigDecimal depositRevenue;

    /** 退款金额 */
    private BigDecimal refundAmount;

    /** 入住总数 */
    private Integer checkInCount;

    /** 退房总数 */
    private Integer checkOutCount;

    /** 预订总数 */
    private Integer reservationCount;

    /** 新增客户数 */
    private Integer newCustomerCount;

    /** 平均入住率 */
    private BigDecimal avgOccupancyRate;

    /** 平均每日营收 */
    private BigDecimal avgDailyRevenue;

    /** 生成时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
