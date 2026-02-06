package com.hotel.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 退房请求
 */
@Data
public class CheckOutRequest {

    /**
     * 额外费用
     */
    private BigDecimal extraCharges;

    /**
     * 折扣
     */
    private BigDecimal discount;

    /**
     * 支付方式：0-现金，1-微信，2-支付宝，3-银行卡，4-挂账
     */
    private Integer paymentMethod;
}
