package com.hotel.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotel.common.Result;
import com.hotel.dto.ReservationRequest;
import com.hotel.entity.Customer;
import com.hotel.entity.Reservation;
import com.hotel.service.ReservationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * 预订管理控制器
 */
@Api(tags = "预订管理")
@RestController
@RequestMapping("/api/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @ApiOperation("分页查询预订")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('reservation:list') or hasAuthority('*:*:*')")
    public Result<IPage<Reservation>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String reservationNo,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        Page<Reservation> page = new Page<>(current, size);
        return Result.success(reservationService.page(page, reservationNo, customerName, phone, status, startDate, endDate));
    }

    @ApiOperation("获取预订详情")
    @GetMapping("/{id}")
    public Result<Reservation> getById(@PathVariable Long id) {
        return Result.success(reservationService.getDetailById(id));
    }

    @ApiOperation("创建预订")
    @PostMapping
    @PreAuthorize("hasAuthority('reservation:create') or hasAuthority('*:*:*')")
    public Result<Reservation> create(@RequestBody ReservationRequest request) {
        Reservation reservation = request.getReservation();
        Customer customer = request.getCustomer();
        return Result.success(reservationService.createReservation(reservation, customer));
    }

    @ApiOperation("确认预订")
    @PutMapping("/{id}/confirm")
    @PreAuthorize("hasAuthority('reservation:edit') or hasAuthority('*:*:*')")
    public Result<Void> confirm(@PathVariable Long id) {
        reservationService.confirmReservation(id);
        return Result.success();
    }

    @ApiOperation("取消预订")
    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('reservation:edit') or hasAuthority('*:*:*')")
    public Result<Void> cancel(@PathVariable Long id,
                               @RequestParam(required = false) String reason) {
        reservationService.cancelReservation(id, reason);
        return Result.success();
    }

    @ApiOperation("更新预订")
    @PutMapping
    @PreAuthorize("hasAuthority('reservation:edit') or hasAuthority('*:*:*')")
    public Result<Void> update(@RequestBody Reservation reservation) {
        reservationService.updateById(reservation);
        return Result.success();
    }

    @ApiOperation("删除预订")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('reservation:delete') or hasAuthority('*:*:*')")
    public Result<Void> delete(@PathVariable Long id) {
        reservationService.removeById(id);
        return Result.success();
    }
}
