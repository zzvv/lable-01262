package com.hotel.dto;

import com.hotel.entity.CheckIn;
import com.hotel.entity.Customer;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 入住请求
 */
@Data
public class CheckInRequest {

    /**
     * 预订ID（预订转入住时使用）
     */
    private Long reservationId;

    /**
     * 房间ID（散客入住时使用）
     */
    private Long roomId;

    /**
     * 押金
     */
    private BigDecimal deposit;

    /**
     * 入住信息（散客入住时使用）
     */
    private CheckIn checkIn;

    /**
     * 客户信息（散客入住时使用）
     */
    private Customer customer;
}
