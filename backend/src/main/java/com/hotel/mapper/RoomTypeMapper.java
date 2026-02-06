package com.hotel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hotel.entity.RoomType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 房型Mapper
 */
@Mapper
public interface RoomTypeMapper extends BaseMapper<RoomType> {

    /**
     * 统计房型的可用房间数
     */
    @Select("SELECT COUNT(*) FROM room WHERE room_type_id = #{roomTypeId} AND status = 0 AND deleted = 0")
    Integer countAvailableRooms(@Param("roomTypeId") Long roomTypeId);
}
