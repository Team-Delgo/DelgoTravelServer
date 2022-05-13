package com.delgo.config.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

import static org.quartz.JobBuilder.newJob;

@Slf4j
@Configuration
public class JobSetting {
    @Autowired
    private Scheduler scheduler;

    @PostConstruct
    public void start_refreshPriceJob(){
        JobDetail jobDetail = buildJobDetail(refreshPriceJob.class, new HashMap());
        try{
            scheduler.scheduleJob(jobDetail, buildJobTrigger("*/10 * * * * ?"));
        } catch(SchedulerException e){
            e.printStackTrace();
        }
    }

    @PostConstruct
    public void start_checkBookingStateJob(){
        JobDetail jobDetail = buildJobDetail(checkBookingStateJob.class, new HashMap());
        try{
            scheduler.scheduleJob(jobDetail, buildJobTrigger("*/20 * * * * ?"));
        } catch(SchedulerException e){
            e.printStackTrace();
        }
    }

    public Trigger buildJobTrigger(String scheduleExp){
        return TriggerBuilder.newTrigger()
                .withSchedule(CronScheduleBuilder.cronSchedule(scheduleExp)).build();
    }

    public JobDetail buildJobDetail(Class job, Map params){
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.putAll(params);

        return newJob(job).usingJobData(jobDataMap).build();
    }
}
