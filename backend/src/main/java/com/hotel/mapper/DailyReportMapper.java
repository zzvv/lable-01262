package com.hotel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hotel.entity.DailyReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;

/**
 * 日报表Mapper
 */
@Mapper
public interface DailyReportMapper extends BaseMapper<DailyReport> {

    @Select("SELECT * FROM daily_report WHERE report_date = #{date} LIMIT 1")
    DailyReport selectByDate(@Param("date") LocalDate date);
}
