package com.hotel.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hotel.entity.Consumption;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface ConsumptionMapper extends BaseMapper<Consumption> {

    IPage<Consumption> selectConsumptionPage(
            IPage<Consumption> page,
            @Param("checkInId") Long checkInId,
            @Param("roomNumber") String roomNumber,
            @Param("status") Integer status,
            @Param("isBilled") Integer isBilled,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
