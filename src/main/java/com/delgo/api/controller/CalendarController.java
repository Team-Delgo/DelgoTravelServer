package com.delgo.api.controller;

import com.delgo.api.comm.CommController;
import com.delgo.api.dto.CalendarDTO;
import com.delgo.api.service.CalendarService;
import com.delgo.api.service.PhotoService;
import com.delgo.api.service.PlaceService;
import com.delgo.api.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/calendar")
public class CalendarController extends CommController {

    private final RoomService roomService;
    private final PlaceService placeService;
    private final PhotoService photoService;
    private final CalendarService calendarService;

    @GetMapping("/detailroom")
    public ResponseEntity getDetailRoomCalendar(@RequestParam Integer roomId) {
        return SuccessReturn(new CalendarDTO(
                photoService.getDetailRoomPhotoList(roomId),
                calendarService.getDetailRoomCalendar(roomId),
                placeService.getPlaceNotice(roomService.getRoomById(roomId).getPlaceId())
        ));
    }
}
