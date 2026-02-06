package com.hotel.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotel.common.Result;
import com.hotel.entity.Room;
import com.hotel.service.RoomService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 房间管理控制器
 */
@Api(tags = "房间管理")
@RestController
@RequestMapping("/api/room")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @ApiOperation("分页查询房间")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('room:list:list') or hasAuthority('*:*:*')")
    public Result<IPage<Room>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String roomNumber,
            @RequestParam(required = false) Long roomTypeId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer floor) {
        Page<Room> page = new Page<>(current, size);
        return Result.success(roomService.page(page, roomNumber, roomTypeId, status, floor));
    }

    @ApiOperation("查询可用房间")
    @GetMapping("/available")
    public Result<List<Room>> getAvailableRooms(@RequestParam Long roomTypeId) {
        return Result.success(roomService.getAvailableRooms(roomTypeId));
    }

    @ApiOperation("获取房间详情")
    @GetMapping("/{id}")
    public Result<Room> getById(@PathVariable Long id) {
        return Result.success(roomService.getDetailById(id));
    }

    @ApiOperation("创建房间")
    @PostMapping
    @PreAuthorize("hasAuthority('room:list:add') or hasAuthority('*:*:*')")
    public Result<Void> create(@RequestBody Room room) {
        roomService.save(room);
        return Result.success();
    }

    @ApiOperation("更新房间")
    @PutMapping
    @PreAuthorize("hasAuthority('room:list:edit') or hasAuthority('*:*:*')")
    public Result<Void> update(@RequestBody Room room) {
        roomService.updateById(room);
        return Result.success();
    }

    @ApiOperation("更新房间状态")
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('room:list:edit') or hasAuthority('*:*:*')")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        roomService.updateStatus(id, status);
        return Result.success();
    }

    @ApiOperation("删除房间")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('room:list:delete') or hasAuthority('*:*:*')")
    public Result<Void> delete(@PathVariable Long id) {
        roomService.removeById(id);
        return Result.success();
    }

    @ApiOperation("统计各状态房间数量")
    @GetMapping("/stats/status")
    public Result<List<Map<String, Object>>> countByStatus() {
        return Result.success(roomService.countByStatus());
    }

    @ApiOperation("统计各楼层房间数量")
    @GetMapping("/stats/floor")
    public Result<List<Map<String, Object>>> countByFloor() {
        return Result.success(roomService.countByFloor());
    }
}
