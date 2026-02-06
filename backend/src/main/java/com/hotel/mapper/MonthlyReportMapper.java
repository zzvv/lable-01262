package com.hotel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hotel.entity.MonthlyReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 月报表Mapper
 */
@Mapper
public interface MonthlyReportMapper extends BaseMapper<MonthlyReport> {

    @Select("SELECT * FROM monthly_report WHERE report_year = #{year} AND report_month = #{month} LIMIT 1")
    MonthlyReport selectByYearMonth(@Param("year") Integer year, @Param("month") Integer month);
}
