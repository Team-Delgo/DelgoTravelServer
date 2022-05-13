package com.delgo.config.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.time.LocalTime;

@Slf4j
public class refreshPriceJob extends QuartzJobBean {
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // 스케줄러에 의해 돌아갈 코드 작성
        LocalTime now = LocalTime.now();
        System.out.println(now + ": 10초 단위 스케줄러 테스트");
    }
}