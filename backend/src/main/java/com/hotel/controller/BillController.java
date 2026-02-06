package com.hotel.controller;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotel.common.Result;
import com.hotel.dto.BillExportVO;
import com.hotel.entity.Bill;
import com.hotel.service.BillService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 账单管理控制器
 */
@Api(tags = "账单管理")
@RestController
@RequestMapping("/api/bill")
@RequiredArgsConstructor
public class BillController {

    private final BillService billService;

    @ApiOperation("分页查询账单")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('finance:bill:list') or hasAuthority('*:*:*')")
    public Result<IPage<Bill>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String billNo,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) Integer billType,
            @RequestParam(required = false) Integer paymentStatus,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        Page<Bill> page = new Page<>(current, size);
        return Result.success(billService.page(page, billNo, customerName, billType, paymentStatus, startDate, endDate));
    }

    @ApiOperation("导出账单数据")
    @GetMapping("/export")
    @PreAuthorize("hasAuthority('finance:bill:export') or hasAuthority('*:*:*')")
    public void export(HttpServletResponse response,
                       @RequestParam(required = false) String billNo,
                       @RequestParam(required = false) String customerName,
                       @RequestParam(required = false) Integer billType,
                       @RequestParam(required = false) Integer paymentStatus,
                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) throws Exception {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("账单数据", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        
        List<BillExportVO> list = billService.listForExport(billNo, customerName, billType, paymentStatus, startDate, endDate);
        EasyExcel.write(response.getOutputStream(), BillExportVO.class).sheet("账单数据").doWrite(list);
    }

    @ApiOperation("根据入住ID查询账单")
    @GetMapping("/checkin/{checkInId}")
    public Result<List<Bill>> getByCheckInId(@PathVariable Long checkInId) {
        return Result.success(billService.getByCheckInId(checkInId));
    }

    @ApiOperation("获取账单详情")
    @GetMapping("/{id}")
    public Result<Bill> getById(@PathVariable Long id) {
        return Result.success(billService.getById(id));
    }

    @ApiOperation("支付账单")
    @PutMapping("/{id}/pay")
    @PreAuthorize("hasAuthority('finance:bill:edit') or hasAuthority('*:*:*')")
    public Result<Void> pay(@PathVariable Long id, @RequestParam Integer paymentMethod) {
        billService.payBill(id, paymentMethod);
        return Result.success();
    }

    @ApiOperation("统计今日营收")
    @GetMapping("/stats/today")
    public Result<BigDecimal> sumTodayRevenue() {
        return Result.success(billService.sumTodayRevenue());
    }

    @ApiOperation("统计本月营收")
    @GetMapping("/stats/month")
    public Result<BigDecimal> sumMonthRevenue() {
        return Result.success(billService.sumMonthRevenue());
    }

    @ApiOperation("按账单类型统计")
    @GetMapping("/stats/type")
    public Result<List<Map<String, Object>>> sumByBillType() {
        return Result.success(billService.sumByBillType());
    }
}
