package com.hotel.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotel.common.Result;
import com.hotel.entity.SysUser;
import com.hotel.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理控制器
 */
@Api(tags = "用户管理")
@RestController
@RequestMapping("/api/system/user")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService userService;

    @ApiOperation("分页查询用户")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('system:user:list') or hasAuthority('*:*:*')")
    public Result<IPage<SysUser>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Integer status) {
        Page<SysUser> page = new Page<>(current, size);
        IPage<SysUser> result = userService.page(page, username, phone, status);
        // 清除密码
        result.getRecords().forEach(user -> user.setPassword(null));
        return Result.success(result);
    }

    @ApiOperation("获取用户详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:list') or hasAuthority('*:*:*')")
    public Result<SysUser> getById(@PathVariable Long id) {
        SysUser user = userService.getById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }

    @ApiOperation("创建用户")
    @PostMapping
    @PreAuthorize("hasAuthority('system:user:add') or hasAuthority('*:*:*')")
    public Result<Void> create(@RequestBody SysUser user,
                               @RequestParam(required = false) List<Long> roleIds) {
        userService.createUser(user, roleIds);
        return Result.success();
    }

    @ApiOperation("更新用户")
    @PutMapping
    @PreAuthorize("hasAuthority('system:user:edit') or hasAuthority('*:*:*')")
    public Result<Void> update(@RequestBody SysUser user,
                               @RequestParam(required = false) List<Long> roleIds) {
        userService.updateUser(user, roleIds);
        return Result.success();
    }

    @ApiOperation("删除用户")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:delete') or hasAuthority('*:*:*')")
    public Result<Void> delete(@PathVariable Long id) {
        userService.removeById(id);
        return Result.success();
    }

    @ApiOperation("重置密码")
    @PutMapping("/{id}/password")
    @PreAuthorize("hasAuthority('system:user:edit') or hasAuthority('*:*:*')")
    public Result<Void> resetPassword(@PathVariable Long id,
                                      @RequestParam String newPassword) {
        userService.resetPassword(id, newPassword);
        return Result.success();
    }
}
