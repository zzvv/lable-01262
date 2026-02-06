package com.hotel.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotel.common.Result;
import com.hotel.entity.RoomType;
import com.hotel.service.RoomTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 房型管理控制器
 */
@Api(tags = "房型管理")
@RestController
@RequestMapping("/api/room/type")
@RequiredArgsConstructor
public class RoomTypeController {

    private final RoomTypeService roomTypeService;

    @ApiOperation("分页查询房型")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('room:type:list') or hasAuthority('*:*:*')")
    public Result<IPage<RoomType>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String typeName,
            @RequestParam(required = false) Integer status) {
        Page<RoomType> page = new Page<>(current, size);
        return Result.success(roomTypeService.page(page, typeName, status));
    }

    @ApiOperation("查询所有房型")
    @GetMapping("/list")
    public Result<List<RoomType>> list() {
        return Result.success(roomTypeService.listAll());
    }

    @ApiOperation("获取房型详情")
    @GetMapping("/{id}")
    public Result<RoomType> getById(@PathVariable Long id) {
        return Result.success(roomTypeService.getById(id));
    }

    @ApiOperation("创建房型")
    @PostMapping
    @PreAuthorize("hasAuthority('room:type:add') or hasAuthority('*:*:*')")
    public Result<Void> create(@RequestBody RoomType roomType) {
        roomTypeService.save(roomType);
        return Result.success();
    }

    @ApiOperation("更新房型")
    @PutMapping
    @PreAuthorize("hasAuthority('room:type:edit') or hasAuthority('*:*:*')")
    public Result<Void> update(@RequestBody RoomType roomType) {
        roomTypeService.updateById(roomType);
        return Result.success();
    }

    @ApiOperation("删除房型")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('room:type:delete') or hasAuthority('*:*:*')")
    public Result<Void> delete(@PathVariable Long id) {
        roomTypeService.removeById(id);
        return Result.success();
    }
}
