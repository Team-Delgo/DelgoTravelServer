package com.delgo.api.service;

import com.delgo.api.dto.DateDTO;
import com.delgo.api.repository.PriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalendarService {

    private final PriceRepository priceRepository;

    // RoomId로 날짜별 가격 조회
    public List<DateDTO> getDetailRoomCalendar(int roomId) {
        return priceRepository.findByRoomId(roomId).stream().map(p -> {
            String price = p.getPrice()
                    .replace(",", "")
                    .replace("원", "");
            return new DateDTO(p.getPriceDate(), Integer.parseInt(price), p.getIsBooking());
        }).collect(Collectors.toList());
    }
}
