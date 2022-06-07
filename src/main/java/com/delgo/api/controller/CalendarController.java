package com.delgo.api.controller;

import com.delgo.api.comm.CommController;
import com.delgo.api.domain.photo.DetailRoomPhoto;
import com.delgo.api.dto.CalendarDTO;
import com.delgo.api.dto.DateDTO;
import com.delgo.api.service.CalendarService;
import com.delgo.api.service.PhotoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/calendar")
public class CalendarController extends CommController {

    private final CalendarService calendarService;
    private final PhotoService photoService;

    @GetMapping("/getDetailRoomCalendarData")
    public ResponseEntity getDetailRoomCalendarData(@RequestParam Integer roomId) {
        List<DetailRoomPhoto> detailPhotos = photoService.getDetailRoomPhotoList(roomId);
        List<DateDTO> dateList = calendarService.getDetailRoomCalendarData(roomId);

        return SuccessReturn(new CalendarDTO(detailPhotos, dateList));
    }
}
