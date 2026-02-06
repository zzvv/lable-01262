package com.hotel.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotel.common.Result;
import com.hotel.entity.SysRole;
import com.hotel.service.SysRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器
 */
@Api(tags = "角色管理")
@RestController
@RequestMapping("/api/system/role")
@RequiredArgsConstructor
public class SysRoleController {

    private final SysRoleService roleService;

    @ApiOperation("分页查询角色")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('system:role:list') or hasAuthority('*:*:*')")
    public Result<IPage<SysRole>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) Integer status) {
        Page<SysRole> page = new Page<>(current, size);
        return Result.success(roleService.page(page, roleName, status));
    }

    @ApiOperation("查询所有角色")
    @GetMapping("/list")
    public Result<List<SysRole>> list() {
        return Result.success(roleService.list());
    }

    @ApiOperation("获取角色详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:list') or hasAuthority('*:*:*')")
    public Result<SysRole> getById(@PathVariable Long id) {
        return Result.success(roleService.getById(id));
    }

    @ApiOperation("获取角色菜单ID列表")
    @GetMapping("/{id}/menus")
    @PreAuthorize("hasAuthority('system:role:list') or hasAuthority('*:*:*')")
    public Result<List<Long>> getMenuIds(@PathVariable Long id) {
        return Result.success(roleService.getMenuIdsByRoleId(id));
    }

    @ApiOperation("创建角色")
    @PostMapping
    @PreAuthorize("hasAuthority('system:role:add') or hasAuthority('*:*:*')")
    public Result<Void> create(@RequestBody SysRole role,
                               @RequestParam(required = false) List<Long> menuIds) {
        roleService.createRole(role, menuIds);
        return Result.success();
    }

    @ApiOperation("更新角色")
    @PutMapping
    @PreAuthorize("hasAuthority('system:role:edit') or hasAuthority('*:*:*')")
    public Result<Void> update(@RequestBody SysRole role,
                               @RequestParam(required = false) List<Long> menuIds) {
        roleService.updateRole(role, menuIds);
        return Result.success();
    }

    @ApiOperation("删除角色")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:delete') or hasAuthority('*:*:*')")
    public Result<Void> delete(@PathVariable Long id) {
        roleService.removeById(id);
        return Result.success();
    }
}
