package com.delgo.api.service;

import com.delgo.api.domain.price.Price;
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

    public List<String> getReservedDateList(int roomId) {
        List<String> reservedDateList = new ArrayList<String>(); // 결과 반환 리스트
        List<Price> bookedList = priceRepository.findByRoomIdAndIsBooking(roomId, 1); // 예약된 방 조회
        bookedList.forEach(price -> {
            String date = price.getPriceDate().replace("-", "");
            reservedDateList.add(date);
        });
        List<Price> waitList = priceRepository.findByRoomIdAndIsWait(roomId, 1); // 예약 대기중인 방 조회
        waitList.forEach(price -> {
            String date = price.getPriceDate().replace("-", "");
            reservedDateList.add(date);
        });

        return reservedDateList;
    }

}
