package com.hotel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("consumption")
public class Consumption implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String consumptionNo;

    private Long checkInId;

    private Long customerId;

    private String itemName;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal amount;

    private Integer status;

    private Integer isBilled;

    private Long billId;

    private Long operatorId;

    private Long cancelOperatorId;

    private LocalDateTime cancelTime;

    private String cancelRemark;

    private String remark;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private Customer customer;

    @TableField(exist = false)
    private CheckIn checkIn;

    @TableField(exist = false)
    private String operatorName;

    @TableField(exist = false)
    private String cancelOperatorName;

    @TableField(exist = false)
    private String roomNumber;
}
