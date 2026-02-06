package com.hotel.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 房型实体
 */
@Data
@TableName("room_type")
public class RoomType implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String typeName;

    private BigDecimal price;

    private String bedType;

    private Integer maxGuests;

    private BigDecimal area;

    private String description;

    private String amenities;

    private String images;

    private Integer status;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 可用房间数（非数据库字段）
     */
    @TableField(exist = false)
    private Integer availableCount;
}
