package com.hotel.controller;

import com.hotel.common.Result;
import com.hotel.entity.SysMenu;
import com.hotel.security.SecurityUtils;
import com.hotel.service.SysMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理控制器
 */
@Api(tags = "菜单管理")
@RestController
@RequestMapping("/api/system/menu")
@RequiredArgsConstructor
public class SysMenuController {

    private final SysMenuService menuService;

    @ApiOperation("查询菜单树")
    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('system:menu:list') or hasAuthority('*:*:*')")
    public Result<List<SysMenu>> tree() {
        return Result.success(menuService.listTree());
    }

    @ApiOperation("查询所有菜单")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:menu:list') or hasAuthority('*:*:*')")
    public Result<List<SysMenu>> list() {
        return Result.success(menuService.listAll());
    }

    @ApiOperation("查询用户菜单")
    @GetMapping("/user")
    public Result<List<SysMenu>> userMenus() {
        Long userId = SecurityUtils.getUserId();
        return Result.success(menuService.listUserMenus(userId));
    }

    @ApiOperation("获取菜单详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:menu:list') or hasAuthority('*:*:*')")
    public Result<SysMenu> getById(@PathVariable Long id) {
        return Result.success(menuService.getById(id));
    }

    @ApiOperation("创建菜单")
    @PostMapping
    @PreAuthorize("hasAuthority('system:menu:add') or hasAuthority('*:*:*')")
    public Result<Void> create(@RequestBody SysMenu menu) {
        menuService.save(menu);
        return Result.success();
    }

    @ApiOperation("更新菜单")
    @PutMapping
    @PreAuthorize("hasAuthority('system:menu:edit') or hasAuthority('*:*:*')")
    public Result<Void> update(@RequestBody SysMenu menu) {
        menuService.updateById(menu);
        return Result.success();
    }

    @ApiOperation("删除菜单")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:menu:delete') or hasAuthority('*:*:*')")
    public Result<Void> delete(@PathVariable Long id) {
        menuService.removeById(id);
        return Result.success();
    }
}
