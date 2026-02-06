package com.hotel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 日报表实体
 */
@Data
@TableName("daily_report")
public class DailyReport implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 报表日期 */
    private LocalDate reportDate;

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

    /** 入住数 */
    private Integer checkInCount;

    /** 退房数 */
    private Integer checkOutCount;

    /** 预订数 */
    private Integer reservationCount;

    /** 新增客户数 */
    private Integer newCustomerCount;

    /** 入住率 */
    private BigDecimal occupancyRate;

    /** 生成时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
