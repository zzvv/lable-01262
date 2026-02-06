package com.hotel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 入住记录实体
 */
@Data
@TableName("check_in")
public class CheckIn implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String checkInNo;

    private Long reservationId;

    private Long customerId;

    private Long roomId;

    private LocalDateTime checkInTime;

    private LocalDateTime checkOutTime;

    private LocalDate expectedCheckOut;

    private Integer nights;

    private BigDecimal roomPrice;

    private BigDecimal totalPrice;

    private BigDecimal deposit;

    private BigDecimal extraCharges;

    private BigDecimal discount;

    private BigDecimal actualAmount;

    private BigDecimal paidAmount;

    private Integer status;

    private String remark;

    private Long operatorId;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 客户信息（非数据库字段）
     */
    @TableField(exist = false)
    private Customer customer;

    /**
     * 房间信息（非数据库字段）
     */
    @TableField(exist = false)
    private Room room;
}
