package com.hotel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 预订实体
 */
@Data
@TableName("reservation")
public class Reservation implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String reservationNo;

    private Long customerId;

    private Long roomTypeId;

    private Long roomId;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private Integer nights;

    private Integer roomCount;

    private Integer guestCount;

    private BigDecimal totalPrice;

    private BigDecimal deposit;

    private Integer status;

    private Integer source;

    private String remark;

    private String cancelReason;

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
     * 房型信息（非数据库字段）
     */
    @TableField(exist = false)
    private RoomType roomType;

    /**
     * 房间信息（非数据库字段）
     */
    @TableField(exist = false)
    private Room room;
}
