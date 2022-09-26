package com.delgo.api.comm.quartz.job;

import com.delgo.api.domain.room.Room;
import com.delgo.api.service.PriceService;
import com.delgo.api.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class RefreshPriceJob extends QuartzJobBean {

    private final RoomService roomService;
    private final PriceService priceService;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        LocalDateTime startTime = LocalDateTime.now();
        System.out.println("************ RefreshPriceJob Execute ************"  + startTime);

        // 스케줄러에 의해 돌아갈 코드 작성
        List<Room> roomList = roomService.selectAll();
        priceService.crawlingProcess(roomList);

        LocalDateTime endTime = LocalDateTime.now();
        System.out.println("************ RefreshPriceJob Exit ************" + endTime);
        System.out.println("총 걸린 시간 : " + ChronoUnit.MINUTES.between(startTime, endTime));

    }
}