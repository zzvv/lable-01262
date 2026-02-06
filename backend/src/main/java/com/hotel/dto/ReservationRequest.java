package com.hotel.dto;

import com.hotel.entity.Customer;
import com.hotel.entity.Reservation;
import lombok.Data;

/**
 * 预订请求
 */
@Data
public class ReservationRequest {

    private Reservation reservation;
    private Customer customer;
}
