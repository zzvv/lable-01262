package com.hotel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 账单实体
 */
@Data
@TableName("bill")
public class Bill implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String billNo;

    private Long checkInId;

    private Long customerId;

    private Integer billType;

    private String itemName;

    private BigDecimal amount;

    private Integer paymentMethod;

    private Integer paymentStatus;

    private LocalDateTime paymentTime;

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
     * 入住信息（非数据库字段）
     */
    @TableField(exist = false)
    private CheckIn checkIn;
}
