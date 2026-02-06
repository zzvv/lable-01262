package com.hotel.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.common.BusinessException;
import com.hotel.common.Constants;
import com.hotel.entity.Room;
import com.hotel.mapper.RoomMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 房间服务
 */
@Service
@RequiredArgsConstructor
public class RoomService extends ServiceImpl<RoomMapper, Room> {

    /**
     * 分页查询房间
     */
    public IPage<Room> page(Page<Room> page, String roomNumber, Long roomTypeId, Integer status, Integer floor) {
        return baseMapper.selectRoomPage(page, roomNumber, roomTypeId, status, floor);
    }

    /**
     * 根据ID查询房间
     */
    public Room getDetailById(Long id) {
        return baseMapper.selectRoomById(id);
    }

    /**
     * 查询可用房间
     */
    public List<Room> getAvailableRooms(Long roomTypeId) {
        return baseMapper.selectAvailableRooms(roomTypeId);
    }

    /**
     * 更新房间状态
     */
    public void updateStatus(Long id, Integer status) {
        Room room = getById(id);
        if (room == null) {
            throw new BusinessException("房间不存在");
        }
        baseMapper.updateStatus(id, status);
    }

    /**
     * 分配房间（预订时）
     */
    public Room assignRoom(Long roomTypeId) {
        List<Room> availableRooms = getAvailableRooms(roomTypeId);
        if (availableRooms.isEmpty()) {
            throw new BusinessException("该房型暂无可用房间");
        }
        Room room = availableRooms.get(0);
        updateStatus(room.getId(), Constants.RoomStatus.RESERVED);
        return room;
    }

    /**
     * 统计各状态房间数量
     */
    public List<Map<String, Object>> countByStatus() {
        return baseMapper.countByStatus();
    }

    /**
     * 统计各楼层房间数量
     */
    public List<Map<String, Object>> countByFloor() {
        return baseMapper.countByFloor();
    }
}
