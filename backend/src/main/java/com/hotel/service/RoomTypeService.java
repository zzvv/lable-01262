package com.hotel.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hotel.entity.RoomType;
import com.hotel.mapper.RoomTypeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 房型服务
 */
@Service
@RequiredArgsConstructor
public class RoomTypeService extends ServiceImpl<RoomTypeMapper, RoomType> {

    /**
     * 分页查询房型
     */
    public IPage<RoomType> page(Page<RoomType> page, String typeName, Integer status) {
        LambdaQueryWrapper<RoomType> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(typeName), RoomType::getTypeName, typeName)
               .eq(status != null, RoomType::getStatus, status)
               .orderByAsc(RoomType::getPrice);

        IPage<RoomType> result = page(page, wrapper);

        // 查询每个房型的可用房间数
        result.getRecords().forEach(roomType -> {
            Integer availableCount = baseMapper.countAvailableRooms(roomType.getId());
            roomType.setAvailableCount(availableCount);
        });

        return result;
    }

    /**
     * 查询所有房型
     */
    public List<RoomType> listAll() {
        List<RoomType> list = list(new LambdaQueryWrapper<RoomType>()
                .eq(RoomType::getStatus, 1)
                .orderByAsc(RoomType::getPrice));

        // 查询每个房型的可用房间数
        list.forEach(roomType -> {
            Integer availableCount = baseMapper.countAvailableRooms(roomType.getId());
            roomType.setAvailableCount(availableCount);
        });

        return list;
    }
}
