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
        List<Price> priceList = priceRepository.findByRoomIdAndIsBooking(roomId, 1);
        List<String> dateList = new ArrayList<String>();
        priceList.forEach(price -> dateList.add(price.getPriceDate()));
        return dateList;
    }

}
