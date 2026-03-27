package com.hotel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 消费记录实体类
 */
@Data
@TableName("consumption_record")
public class ConsumptionRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 入住记录ID
     */
    private Long checkInId;

    /**
     * 商品名称
     */
    private String itemName;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 单价
     */
    private BigDecimal unitPrice;

    /**
     * 金额（数量×单价）
     */
    private BigDecimal amount;

    /**
     * 支付方式：0-现金，1-微信，2-支付宝，3-银行卡，4-挂账
     */
    private Integer paymentMethod;

    /**
     * 支付状态：0-未支付，1-已支付，2-已退款
     */
    private Integer paymentStatus;

    /**
     * 支付时间
     */
    private LocalDateTime paymentTime;

    /**
     * 操作员ID
     */
    private Long operatorId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态：0-已作废，1-正常
     */
    private Integer status;

    /**
     * 作废操作人ID
     */
    private Long cancelledBy;

    /**
     * 作废时间
     */
    private LocalDateTime cancelledTime;

    /**
     * 删除标志：0-未删除，1-已删除
     */
    private Integer deleted;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
