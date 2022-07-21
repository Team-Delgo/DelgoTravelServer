package com.delgo.api.comm.quartz.job;

import com.delgo.api.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.time.LocalDate;
import java.time.LocalTime;

@Slf4j
@RequiredArgsConstructor
public class StartTripJob extends QuartzJobBean {

    private final BookingService bookingService;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info(LocalTime.now() + ": StartTripJob Execute");

        String today = LocalDate.now().toString();
        bookingService.fixToTrip(today);

        log.info(LocalTime.now() + ": StartTripJob Exit");
    }
}
