package com.delgo.api.comm.quartz.job;

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
        log.info(LocalTime.now() + ": DeleteYesterdayDataJob  Execute");

        String yesterday = LocalDate.now().minusDays(1).toString();
        priceService.deleteYesterdayPrice(yesterday);

    }
}