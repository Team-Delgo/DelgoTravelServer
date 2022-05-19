package com.delgo.api.controller;

import com.delgo.api.dto.common.ResponseDTO;
import com.delgo.api.service.CalendarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/calendar")
public class CalendarController {

    private final CalendarService calendarService;

    @GetMapping("/getReservedDateList")
    public ResponseEntity getReservedDateList(int roomId){
        List<String> dateList = calendarService.getReservedDateList(roomId);

        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(200).codeMsg("Success").data(dateList).build());
    }
}