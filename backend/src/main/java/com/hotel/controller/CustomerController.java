package com.hotel.controller;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotel.common.Result;
import com.hotel.entity.Customer;
import com.hotel.service.CustomerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 客户管理控制器
 */
@Api(tags = "客户管理")
@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @ApiOperation("分页查询客户")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('customer:list') or hasAuthority('*:*:*')")
    public Result<IPage<Customer>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Integer memberLevel) {
        Page<Customer> page = new Page<>(current, size);
        return Result.success(customerService.page(page, name, phone, memberLevel));
    }

    @ApiOperation("导出客户数据")
    @GetMapping("/export")
    @PreAuthorize("hasAuthority('customer:export') or hasAuthority('*:*:*')")
    public void export(HttpServletResponse response,
                       @RequestParam(required = false) String name,
                       @RequestParam(required = false) String phone,
                       @RequestParam(required = false) Integer memberLevel) throws Exception {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("客户数据", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        
        List<Customer> list = customerService.listForExport(name, phone, memberLevel);
        EasyExcel.write(response.getOutputStream(), Customer.class).sheet("客户数据").doWrite(list);
    }

    @ApiOperation("根据手机号查询客户")
    @GetMapping("/phone/{phone}")
    public Result<Customer> getByPhone(@PathVariable String phone) {
        return Result.success(customerService.getByPhone(phone));
    }

    @ApiOperation("根据身份证号查询客户")
    @GetMapping("/idcard/{idCard}")
    public Result<Customer> getByIdCard(@PathVariable String idCard) {
        return Result.success(customerService.getByIdCard(idCard));
    }

    @ApiOperation("获取客户详情")
    @GetMapping("/{id}")
    public Result<Customer> getById(@PathVariable Long id) {
        return Result.success(customerService.getById(id));
    }

    @ApiOperation("创建客户")
    @PostMapping
    @PreAuthorize("hasAuthority('customer:add') or hasAuthority('*:*:*')")
    public Result<Void> create(@RequestBody Customer customer) {
        customerService.save(customer);
        return Result.success();
    }

    @ApiOperation("更新客户")
    @PutMapping
    @PreAuthorize("hasAuthority('customer:edit') or hasAuthority('*:*:*')")
    public Result<Void> update(@RequestBody Customer customer) {
        customerService.updateById(customer);
        return Result.success();
    }

    @ApiOperation("删除客户")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('customer:delete') or hasAuthority('*:*:*')")
    public Result<Void> delete(@PathVariable Long id) {
        customerService.removeById(id);
        return Result.success();
    }
}
