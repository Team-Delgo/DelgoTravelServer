package com.delgo.api.config.quartz.job;

import com.delgo.api.domain.Room;
import com.delgo.api.service.PriceService;
import com.delgo.api.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.time.LocalTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class RefreshPriceJob extends QuartzJobBean {

    private final RoomService roomService;
    private final PriceService priceService;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // 스케줄러에 의해 돌아갈 코드 작성
        List<Room> roomList = roomService.selectAll();
        roomList.forEach(room -> {
            System.out.println("======================================================================================");
            priceService.crawlingProcess(room.getRoomId(), room.getPlaceId(), room.getCrawlingUrl());
            System.out.println("======================================================================================");
        });

        LocalTime now = LocalTime.now();
        log.info(now + ": RefreshPriceJob Execute");
    }
}