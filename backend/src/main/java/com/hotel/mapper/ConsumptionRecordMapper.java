package com.hotel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hotel.entity.ConsumptionRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 消费记录 Mapper 接口
 */
@Mapper
public interface ConsumptionRecordMapper extends BaseMapper<ConsumptionRecord> {
}
