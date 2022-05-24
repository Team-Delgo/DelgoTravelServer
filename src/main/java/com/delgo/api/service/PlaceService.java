package com.delgo.api.service;

import com.delgo.api.domain.Place;
import com.delgo.api.domain.Room;
import com.delgo.api.domain.price.Price;
import com.delgo.api.repository.PlaceRepository;
import com.delgo.api.repository.PriceRepository;
import com.delgo.api.repository.RoomRepository;
import com.delgo.api.repository.specification.PlaceSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final RoomRepository roomRepository;
    private final PriceRepository priceRepository;

    public List<Place> getAllPlace() {
        return placeRepository.findAll();
    }

    public Optional<Place> findByPlaceId(int placeId) {
        return placeRepository.findByPlaceId(placeId);
    }

    // 검색
    public List<Place> searchPlace(Map<String, Object> searchKeys) {
        return placeRepository.findAll(PlaceSpecification.searchPlace(searchKeys));
    }

    public String getLowestPrice(int placeId) {
        List<Price> list = priceRepository.findByPlaceId(placeId);

//        if (list.isEmpty()) return ""; // 예외처리

        List<Integer> priceList = new ArrayList<Integer>();
        list.forEach(p -> {
            String price = p.getPrice();
            if (!price.equals("")) {
                price = price.replace(",", "");
                price = price.replace("원", "");
                priceList.add(Integer.parseInt(price));
            }
        });

        DecimalFormat df = new DecimalFormat("###,###원"); //포맷팅
//        log.info(df.format(Collections.min(priceList)));
        return df.format(Collections.min(priceList)); // 최소가격
    }

    // 최대 2주치만 검색 가능
    public boolean checkBooking(int placeId, LocalDate startDt, LocalDate endDt) {
        Period period = Period.between(startDt, endDt); // 시작, 끝 간격
        List<Room> roomList = roomRepository.findByPlaceId(placeId);
        for (Room room : roomList) {
            // roomId로 예약가능한 날짜 조회
            List<Price> priceList = priceRepository.findByRoomIdAndIsBookingAndPriceDateBetween(room.getRoomId(), 0, startDt.toString(), endDt.toString());
            // 단 한개의 Room이라도 예약 가능하다면 해당 Place는 보여져야 한다. 따라서 return true;
            if (priceList.size() == period.getDays() + 1) return true;
        }
        return false;
    }
}
