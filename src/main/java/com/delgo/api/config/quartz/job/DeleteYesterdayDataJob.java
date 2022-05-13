package com.delgo.api.config.quartz.job;

import com.delgo.api.service.PriceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.time.LocalDate;
import java.time.LocalTime;

@Slf4j
@RequiredArgsConstructor
public class DeleteYesterdayDataJob extends QuartzJobBean {

    private final PriceService priceService;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // 스케줄러에 의해 돌아갈 코드 작성
        LocalTime now = LocalTime.now();
        String yesterday = LocalDate.now().minusDays(1).toString();
        priceService.deleteYesterdayPrice(yesterday);

        log.info(now + ": DeleteYesterdayDataJob  Execute");
    }
}