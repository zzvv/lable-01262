package com.hotel.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotel.common.Result;
import com.hotel.entity.DailyReport;
import com.hotel.entity.MonthlyReport;
import com.hotel.service.ReportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * 报表管理控制器
 */
@Api(tags = "报表管理")
@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @ApiOperation("分页查询日报表")
    @GetMapping("/daily/page")
    @PreAuthorize("hasAuthority('finance:report:list') or hasAuthority('*:*:*')")
    public Result<IPage<DailyReport>> pageDailyReport(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        Page<DailyReport> page = new Page<>(current, size);
        return Result.success(reportService.pageDailyReport(page, startDate, endDate));
    }

    @ApiOperation("分页查询月报表")
    @GetMapping("/monthly/page")
    @PreAuthorize("hasAuthority('finance:report:list') or hasAuthority('*:*:*')")
    public Result<IPage<MonthlyReport>> pageMonthlyReport(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer year) {
        Page<MonthlyReport> page = new Page<>(current, size);
        return Result.success(reportService.pageMonthlyReport(page, year));
    }

    @ApiOperation("手动生成日报表")
    @PostMapping("/daily/generate")
    @PreAuthorize("hasAuthority('finance:report:generate') or hasAuthority('*:*:*')")
    public Result<DailyReport> generateDailyReport(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return Result.success(reportService.generateDailyReport(date));
    }

    @ApiOperation("手动生成月报表")
    @PostMapping("/monthly/generate")
    @PreAuthorize("hasAuthority('finance:report:generate') or hasAuthority('*:*:*')")
    public Result<MonthlyReport> generateMonthlyReport(
            @RequestParam Integer year,
            @RequestParam Integer month) {
        return Result.success(reportService.generateMonthlyReport(year, month));
    }
}
