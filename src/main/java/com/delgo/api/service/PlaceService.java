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

    // 최저가격 계산
    public String getLowestPrice(int placeId, LocalDate startDt, LocalDate endDt) {
        Period period = Period.between(startDt, endDt); // 시작, 끝 간격
        List<Room> roomList = roomRepository.findByPlaceId(placeId); // place에 속한 room 조회
        List<Integer> minPricelist = new ArrayList<Integer>(); // 각 방의 minimum Price 담을 List
        roomList.forEach(room -> {
            // roomId로 예약가능한 날짜 조회
            List<Price> pList = priceRepository.findByRoomIdAndIsBookingAndPriceDateBetween(room.getRoomId(), 0, startDt.toString(), endDt.toString());
            if (pList.size() == period.getDays() + 1) {
                // 4박5일일 경우 총 여행경비는 앞 4일 가격의 합이다. 따라서 마지막 삭제
                pList.remove(pList.size() - 1);
                // 예약가능한 각 방의 가격중 가장 저렴한 가격 조회
                List<Integer> pricelist = new ArrayList<Integer>();
                pList.forEach(p -> {
                    String price = p.getPrice();
                    price = price.replace(",", "");
                    price = price.replace("원", "");
                    if (!price.equals("0"))
                        pricelist.add(Integer.parseInt(price));
                });
                minPricelist.add(Collections.min(pricelist));
            }
        });

        if (minPricelist.size() == 0)
            return "0원";

            DecimalFormat df = new DecimalFormat("###,###원"); //포맷팅
            return df.format(Collections.min(minPricelist)); // place의 최소 값.
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
