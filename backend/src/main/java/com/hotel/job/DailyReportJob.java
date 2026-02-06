package com.hotel.job;

import com.hotel.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 日报表生成定时任务
 * 每天凌晨1点执行，生成前一天的日报表
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DailyReportJob implements Job {

    private final ReportService reportService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("========== 日报表定时任务开始执行 ==========");
        try {
            // 生成昨天的日报表
            LocalDate yesterday = LocalDate.now().minusDays(1);
            reportService.generateDailyReport(yesterday);
            log.info("========== 日报表定时任务执行完成 ==========");
        } catch (Exception e) {
            log.error("日报表定时任务执行失败", e);
            throw new JobExecutionException(e);
        }
    }
}
