package com.hotel.config;

import com.hotel.job.DailyReportJob;
import com.hotel.job.MonthlyReportJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Quartz定时任务配置
 */
@Configuration
public class QuartzConfig {

    /**
     * 日报表任务详情
     */
    @Bean
    public JobDetail dailyReportJobDetail() {
        return JobBuilder.newJob(DailyReportJob.class)
                .withIdentity("dailyReportJob", "reportGroup")
                .withDescription("每日报表生成任务")
                .storeDurably()
                .build();
    }

    /**
     * 日报表触发器 - 每天凌晨1点执行
     */
    @Bean
    public Trigger dailyReportTrigger() {
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder
                .cronSchedule("0 0 1 * * ?")  // 每天凌晨1点
                .withMisfireHandlingInstructionDoNothing();
        
        return TriggerBuilder.newTrigger()
                .forJob(dailyReportJobDetail())
                .withIdentity("dailyReportTrigger", "reportGroup")
                .withDescription("每日报表触发器")
                .withSchedule(scheduleBuilder)
                .build();
    }

    /**
     * 月报表任务详情
     */
    @Bean
    public JobDetail monthlyReportJobDetail() {
        return JobBuilder.newJob(MonthlyReportJob.class)
                .withIdentity("monthlyReportJob", "reportGroup")
                .withDescription("每月报表生成任务")
                .storeDurably()
                .build();
    }

    /**
     * 月报表触发器 - 每月1号凌晨2点执行
     */
    @Bean
    public Trigger monthlyReportTrigger() {
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder
                .cronSchedule("0 0 2 1 * ?")  // 每月1号凌晨2点
                .withMisfireHandlingInstructionDoNothing();
        
        return TriggerBuilder.newTrigger()
                .forJob(monthlyReportJobDetail())
                .withIdentity("monthlyReportTrigger", "reportGroup")
                .withDescription("每月报表触发器")
                .withSchedule(scheduleBuilder)
                .build();
    }
}
