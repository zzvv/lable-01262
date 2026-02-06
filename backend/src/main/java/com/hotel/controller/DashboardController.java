package com.hotel.controller;

import com.hotel.common.Result;
import com.hotel.service.DashboardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 仪表盘控制器
 */
@Api(tags = "仪表盘")
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @ApiOperation("获取统计数据")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        return Result.success(dashboardService.getStatistics());
    }

    @ApiOperation("获取图表数据")
    @GetMapping("/charts")
    public Result<Map<String, Object>> getChartData() {
        return Result.success(dashboardService.getChartData());
    }
}
