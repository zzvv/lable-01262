package com.hotel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hotel.entity.Room;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * 房间Mapper
 */
@Mapper
public interface RoomMapper extends BaseMapper<Room> {

    /**
     * 分页查询房间（带房型信息）
     */
    IPage<Room> selectRoomPage(Page<Room> page, @Param("roomNumber") String roomNumber,
                               @Param("roomTypeId") Long roomTypeId, @Param("status") Integer status,
                               @Param("floor") Integer floor);

    /**
     * 根据ID查询房间（带房型信息）
     */
    Room selectRoomById(@Param("id") Long id);

    /**
     * 查询可用房间
     */
    @Select("SELECT * FROM room WHERE room_type_id = #{roomTypeId} AND status = 0 AND deleted = 0")
    List<Room> selectAvailableRooms(@Param("roomTypeId") Long roomTypeId);

    /**
     * 更新房间状态
     */
    @Update("UPDATE room SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 统计各状态房间数量
     */
    @Select("SELECT status, COUNT(*) as count FROM room WHERE deleted = 0 GROUP BY status")
    List<Map<String, Object>> countByStatus();

    /**
     * 统计各楼层房间数量
     */
    @Select("SELECT floor, COUNT(*) as count FROM room WHERE deleted = 0 GROUP BY floor ORDER BY floor")
    List<Map<String, Object>> countByFloor();

    /**
     * 统计房间总数
     */
    @Select("SELECT COUNT(*) FROM room WHERE deleted = 0")
    Integer countTotal();

    /**
     * 统计入住中房间数
     */
    @Select("SELECT COUNT(*) FROM room WHERE status = 2 AND deleted = 0")
    Integer countOccupied();
}
