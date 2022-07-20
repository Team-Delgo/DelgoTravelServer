package com.delgo.api.comm.quartz;

import com.delgo.api.comm.quartz.job.DeleteExpiredCouponJob;
import com.delgo.api.comm.quartz.job.DeleteYesterdayDataJob;
import com.delgo.api.comm.quartz.job.RefreshPriceJob;
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
        JobDetail jobDetail = buildJobDetail(RefreshPriceJob.class, new HashMap());
        try{
            scheduler.scheduleJob(jobDetail, buildJobTrigger("0 0 */1 * * ?"));
        } catch(SchedulerException e){
            e.printStackTrace();
        }
    }

    @PostConstruct
    public void start_deleteYesterdayDataJob(){
        JobDetail jobDetail = buildJobDetail(DeleteYesterdayDataJob.class, new HashMap());
        try{
            scheduler.scheduleJob(jobDetail, buildJobTrigger("0 0 0 * * ?"));
        } catch(SchedulerException e){
            e.printStackTrace();
        }
    }

    @PostConstruct
    public void start_deleteExpiredCouponJob(){
        JobDetail jobDetail = buildJobDetail(DeleteExpiredCouponJob.class, new HashMap());
        try{
            scheduler.scheduleJob(jobDetail, buildJobTrigger("0 0 0 * * ?"));
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
