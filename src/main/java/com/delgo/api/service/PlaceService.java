package com.delgo.api.service;

import com.delgo.api.domain.photo.DetailPhoto;
import com.delgo.api.domain.Place;
import com.delgo.api.domain.Room;
import com.delgo.api.domain.price.Price;
import com.delgo.api.repository.DetailPhotoRepository;
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
    private final DetailPhotoRepository detailPhotoRepository;

    public List<Place> getWhereToGoData() {
        // 전체 Place 리스트 조회
        List<Place> placeList = placeRepository.findAll();
        if (placeList.size() > 0) {
            // place MainPhoto 설정
            placeList.forEach(place -> {
                Optional<DetailPhoto> mainPhoto = detailPhotoRepository.findByPlaceIdAndIsMain(place.getPlaceId(), 1);
                mainPhoto.ifPresent(photo -> place.setMainPhotoUrl(photo.getUrl()));
            });

            // 예약가능한 Place Check
            placeList.forEach(place -> {
                // place 예약 가능 여부 Check ( 초기 페이지에서는 오늘 기준 1박으로 잡는다. )
                boolean isBooking = checkBooking(place.getPlaceId(), LocalDate.now(), LocalDate.now().plusDays(1));
                if (!isBooking) place.setIsBooking(1);
            });

            // 최저가격 계산
            placeList.forEach(place -> {
                if (place.getIsBooking() == 0) { // 예약가능할 경우 최저가격 계산
                    String lowestPrice = getLowestPrice(place.getPlaceId(), LocalDate.now(), LocalDate.now().plusDays(1));
                    place.setLowestPrice(lowestPrice);
                } else { // 예약 불가능할 경우 0원 입력
                    place.setLowestPrice("0원");
                }
            });
        }
        return placeList;
    }

    public Optional<Place> findByPlaceId(int placeId) {
        // 전체 Place 리스트 조회
        Optional<Place> place = placeRepository.findByPlaceId(placeId);
        if (place.isPresent()) { // place MainPhoto 설정
            Optional<DetailPhoto> mainPhoto = detailPhotoRepository.findByPlaceIdAndIsMain(place.get().getPlaceId(), 1);
            mainPhoto.ifPresent(photo -> place.get().setMainPhotoUrl(photo.getUrl()));
        }

        return place;
    }

    // 검색
    public List<Place> getSearchPlaceListData(Map<String, Object> searchKeys, LocalDate StartDt, LocalDate endDt) {
        // 전체 Place 리스트 조회
        List<Place> placeList = placeRepository.findAll(PlaceSpecification.searchPlace(searchKeys));
        if (placeList.size() > 0) {
            // place MainPhoto 설정
            placeList.forEach(place -> {
                Optional<DetailPhoto> mainPhoto = detailPhotoRepository.findByPlaceIdAndIsMain(place.getPlaceId(), 1);
                mainPhoto.ifPresent(photo -> place.setMainPhotoUrl(photo.getUrl()));
            });

            // 예약가능한 Place Check
            placeList.forEach(place -> {
                // place 예약 가능 여부 Check ( 초기 페이지에서는 오늘 기준 1박으로 잡는다. )
                boolean isBooking = checkBooking(place.getPlaceId(), StartDt, endDt);
                if (isBooking) place.setIsBooking(1);
            });

            // 최저가격 계산
            placeList.forEach(place -> {
                if (place.getIsBooking() == 0) { // 예약가능할 경우 최저가격 계산
                    String lowestPrice = getLowestPrice(place.getPlaceId(), StartDt, endDt);
                    place.setLowestPrice(lowestPrice);
                } else  // 예약 불가능할 경우 0원 입력
                    place.setLowestPrice("0원");
            });
        }
        return placeList;
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
