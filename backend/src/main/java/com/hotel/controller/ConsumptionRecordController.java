package com.hotel.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotel.common.PageResult;
import com.hotel.common.Result;
import com.hotel.entity.ConsumptionRecord;
import com.hotel.service.ConsumptionRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消费记录控制器
 */
@RestController
@RequestMapping("/api/consumption")
@RequiredArgsConstructor
@Api(tags = "消费记录管理")
public class ConsumptionRecordController {

    private final ConsumptionRecordService consumptionRecordService;

    @GetMapping("/page")
    @ApiOperation("分页查询消费记录")
    public Result<PageResult<ConsumptionRecord>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long checkInId,
            @RequestParam(required = false) String itemName,
            @RequestParam(required = false) Integer paymentStatus,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {

        Page<ConsumptionRecord> page = new Page<>(pageNum, pageSize);
        IPage<ConsumptionRecord> result = consumptionRecordService.page(page, checkInId, itemName, paymentStatus, startTime, endTime);
        return Result.success(PageResult.of(result));
    }

    @GetMapping("/{id}")
    @ApiOperation("根据ID查询消费记录")
    public Result<ConsumptionRecord> getById(@PathVariable Long id) {
        return Result.success(consumptionRecordService.getById(id));
    }

    @GetMapping("/checkin/{checkInId}")
    @ApiOperation("根据入住ID查询消费记录列表")
    public Result<List<ConsumptionRecord>> getByCheckInId(@PathVariable Long checkInId) {
        return Result.success(consumptionRecordService.getByCheckInId(checkInId));
    }

    @PostMapping
    @ApiOperation("创建消费记录")
    public Result<ConsumptionRecord> create(@RequestBody ConsumptionRecord record) {
        return Result.success(consumptionRecordService.createConsumption(record));
    }

    @PutMapping
    @ApiOperation("更新消费记录")
    public Result<Void> update(@RequestBody ConsumptionRecord record) {
        consumptionRecordService.updateConsumption(record);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除消费记录")
    public Result<Void> delete(@PathVariable Long id) {
        consumptionRecordService.deleteConsumption(id);
        return Result.success();
    }

    @PutMapping("/{id}/cancel")
    @ApiOperation("作废消费记录")
    public Result<Void> cancel(@PathVariable Long id) {
        consumptionRecordService.cancelConsumption(id);
        return Result.success();
    }

    @PutMapping("/{id}/pay")
    @ApiOperation("支付消费记录")
    public Result<Void> pay(@PathVariable Long id, @RequestParam Integer paymentMethod) {
        consumptionRecordService.payConsumption(id, paymentMethod);
        return Result.success();
    }
}
