package com.hotel.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotel.common.Result;
import com.hotel.dto.CheckInRequest;
import com.hotel.dto.CheckOutRequest;
import com.hotel.entity.CheckIn;
import com.hotel.entity.Customer;
import com.hotel.service.CheckInService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * 入住管理控制器
 */
@Api(tags = "入住管理")
@RestController
@RequestMapping("/api/checkin")
@RequiredArgsConstructor
public class CheckInController {

    private final CheckInService checkInService;

    @ApiOperation("分页查询入住记录")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('checkin:list') or hasAuthority('*:*:*')")
    public Result<IPage<CheckIn>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String checkInNo,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String roomNumber,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        Page<CheckIn> page = new Page<>(current, size);
        return Result.success(checkInService.page(page, checkInNo, customerName, roomNumber, status, startDate, endDate));
    }

    @ApiOperation("获取入住详情")
    @GetMapping("/{id}")
    public Result<CheckIn> getById(@PathVariable Long id) {
        return Result.success(checkInService.getDetailById(id));
    }

    @ApiOperation("预订转入住")
    @PostMapping("/from-reservation")
    @PreAuthorize("hasAuthority('checkin:create') or hasAuthority('*:*:*')")
    public Result<CheckIn> checkInFromReservation(@RequestBody CheckInRequest request) {
        return Result.success(checkInService.checkInFromReservation(
                request.getReservationId(), request.getDeposit()));
    }

    @ApiOperation("散客入住")
    @PostMapping("/walk-in")
    @PreAuthorize("hasAuthority('checkin:create') or hasAuthority('*:*:*')")
    public Result<CheckIn> walkInCheckIn(@RequestBody CheckInRequest request) {
        CheckIn checkIn = request.getCheckIn();
        Customer customer = request.getCustomer();
        return Result.success(checkInService.walkInCheckIn(
                checkIn, customer, request.getRoomId(), request.getDeposit()));
    }

    @ApiOperation("退房结算")
    @PostMapping("/{id}/checkout")
    @PreAuthorize("hasAuthority('checkin:checkout') or hasAuthority('*:*:*')")
    public Result<CheckIn> checkOut(@PathVariable Long id, @RequestBody CheckOutRequest request) {
        return Result.success(checkInService.checkOut(
                id, request.getExtraCharges(), request.getDiscount(), request.getPaymentMethod()));
    }

    @ApiOperation("更新入住记录")
    @PutMapping
    @PreAuthorize("hasAuthority('checkin:edit') or hasAuthority('*:*:*')")
    public Result<Void> update(@RequestBody CheckIn checkIn) {
        checkInService.updateById(checkIn);
        return Result.success();
    }
}
