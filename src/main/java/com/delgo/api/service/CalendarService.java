package com.delgo.api.service;

import com.delgo.api.dto.DateDTO;
import com.delgo.api.repository.PriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalendarService {

    private final PriceRepository priceRepository;


    public List<DateDTO> getDetailRoomCalendarData(int roomId) {
        List<DateDTO> calendarDTOList = new ArrayList<DateDTO>();
        // RoomId로 날짜별 가격 조회
        priceRepository.findByRoomId(roomId).forEach(price -> {
            calendarDTOList.add(new DateDTO(price.getPriceDate(), price.getPrice(), price.getIsBooking()));
        });

        return calendarDTOList;
    }

}
