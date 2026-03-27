package com.hotel.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotel.common.Result;
import com.hotel.dto.ConsumptionRequest;
import com.hotel.entity.Consumption;
import com.hotel.service.ConsumptionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Api(tags = "消费记录管理")
@RestController
@RequestMapping("/api/consumption")
@RequiredArgsConstructor
public class ConsumptionController {

    private final ConsumptionService consumptionService;

    @ApiOperation("分页查询消费记录")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('finance:consumption:list') or hasAuthority('*:*:*')")
    public Result<IPage<Consumption>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long checkInId,
            @RequestParam(required = false) String roomNumber,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer isBilled,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate) {
        Page<Consumption> page = new Page<>(current, size);
        return Result.success(consumptionService.page(page, checkInId, roomNumber, status, isBilled, startDate, endDate));
    }

    @ApiOperation("根据入住ID查询消费记录")
    @GetMapping("/checkin/{checkInId}")
    public Result<List<Consumption>> getByCheckInId(@PathVariable Long checkInId) {
        return Result.success(consumptionService.getByCheckInId(checkInId));
    }

    @ApiOperation("获取消费记录详情")
    @GetMapping("/{id}")
    public Result<Consumption> getById(@PathVariable Long id) {
        return Result.success(consumptionService.getDetailById(id));
    }

    @ApiOperation("创建消费记录")
    @PostMapping
    @PreAuthorize("hasAuthority('finance:consumption:add') or hasAuthority('*:*:*')")
    public Result<Consumption> create(@Valid @RequestBody ConsumptionRequest request) {
        return Result.success(consumptionService.createConsumption(request));
    }

    @ApiOperation("作废消费记录")
    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('finance:consumption:edit') or hasAuthority('*:*:*')")
    public Result<Void> cancel(@PathVariable Long id, @RequestBody(required = false) Map<String, String> body) {
        String cancelRemark = body != null ? body.get("cancelRemark") : null;
        consumptionService.cancelConsumption(id, cancelRemark);
        return Result.success();
    }

    @ApiOperation("批量结算消费记录到账单")
    @PostMapping("/settle/{checkInId}")
    @PreAuthorize("hasAuthority('finance:consumption:settle') or hasAuthority('*:*:*')")
    public Result<Void> settleToBill(@PathVariable Long checkInId, @RequestBody Map<String, Integer> body) {
        Integer paymentMethod = body.get("paymentMethod");
        consumptionService.settleConsumptionToBill(checkInId, paymentMethod);
        return Result.success();
    }
}
