package com.delgo.api.comm.quartz.job;

import com.delgo.api.repository.CouponRepository;
import com.delgo.api.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.time.LocalDate;
import java.time.LocalTime;

@Slf4j
@RequiredArgsConstructor
public class DeleteExpiredCouponJob extends QuartzJobBean {

    private final CouponRepository couponRepository;
    private final CouponService couponService;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info(LocalTime.now() + ": DeleteExpiredCouponJob  Execute");

        String yesterday = LocalDate.now().minusDays(1).toString();
        couponService.deleteExpiredCoupon(yesterday);

        log.info(LocalTime.now() + ": DeleteExpiredCouponJob  Exit");
    }
}