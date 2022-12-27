package com.delgo.api.service;

import com.delgo.api.domain.Wish;
import com.delgo.api.domain.place.Place;
import com.delgo.api.domain.place.PlaceNotice;
import com.delgo.api.domain.price.Price;
import com.delgo.api.repository.*;
import com.delgo.api.repository.specification.PlaceSpecification;
import com.delgo.api.service.photo.DetailPhotoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PlaceService {

    private final RoomRepository roomRepository;
    private final PlaceRepository placeRepository;
    private final PlaceNoticeRepository placeNoticeRepository;

    private final WishService wishService;
    private final PriceService priceService;
    private final DetailPhotoService detailPhotoService;

    public Place register(Place place){
        return placeRepository.save(place);
    }

    // 전체 Place 리스트 조회
    public List<Place> getPlaceAll(int userId) {
        return placeRepository.findAll().stream()
                .peek(place -> setOption(place, LocalDate.now(), LocalDate.now().plusDays(1), userId))
                .collect(Collectors.toList());
    }

    // PlaceId로 Place 조회
    public Place getPlaceById(int placeId) {
        return placeRepository.findByPlaceId(placeId)
                .orElseThrow(() -> new NullPointerException("NOT FOUND PLACE"));
    }

    // 검색조건에 맞는 Place 조회
    public List<Place> getSearch(Map<String, Object> searchKeys) {
        return placeRepository.findAll(PlaceSpecification.searchPlace(searchKeys));
    }

    public List<PlaceNotice> getPlaceNotice(int placeId) {
        return placeNoticeRepository.findByPlaceId(placeId).stream()
                .peek(notice -> {
                    String[] contents = notice.getContent().split("\r\n");
                    notice.setContents(Arrays.asList(contents));
                }).collect(Collectors.toList());
    }

    // 예약가능 여부 조회
    public boolean checkCanBooking(int placeId, LocalDate startDt, LocalDate endDt) {
        return roomRepository.findByPlaceId(placeId).stream().anyMatch(room -> {
            List<Price> canBookingDates = priceService.getCanBookingDates(room.getRoomId(), startDt, endDt);
            return canBookingDates.size() == (Period.between(startDt, endDt).getDays() + 1); // 단 한개의 Room이라도 예약 가능하다면 해당 Place는 보여져야 한다. 따라서 return true;
        });
    }

    // 가격조회 및 최저가격 계산
    public String getLowestPrice(int placeId, LocalDate startDt, LocalDate endDt) {
        List<Integer> minPriceList = roomRepository.findByPlaceId(placeId).stream()
                .map(room -> {
                    List<Price> canBookingDates = priceService.getCanBookingDates(room.getRoomId(), startDt, endDt);
                    if (canBookingDates.size() == Period.between(startDt, endDt).getDays() + 1) {
                        canBookingDates.remove(canBookingDates.size() - 1);  // 4박5일일 경우 총 여행경비는 앞 4일 가격의 합이다. 따라서 마지막 삭제
                        return canBookingDates.stream().mapToInt(Price::priceToInt).sum(); // 예약가능한 각 방의 가격중 가장 저렴한 가격 조회
                    } else
                        return 0;})
                .filter(num -> num != 0)
                .collect(Collectors.toList());

        DecimalFormat df = new DecimalFormat("###,###원"); //포맷팅
        return (minPriceList.size() == 0) ? "0원" : df.format(Collections.min(minPriceList));
    }

    public Place setOption(Place place, LocalDate startDt, LocalDate endDt, int userId){
        place.setMainPhotoUrl(detailPhotoService.getMainPhotoUrl(place.getPlaceId())); // place MainPhoto 설정
        place.setIsBooking(!checkCanBooking(place.getPlaceId(), startDt, endDt)); // Place Booking Check ( 예약가능할 경우 isBooking false 이다 ).
        place.setLowestPrice((place.getIsBooking()) ? "0원" : getLowestPrice(place.getPlaceId(), startDt, endDt));  // 최저가격 계산 // 예약 불가능할 경우 0원 입력
        place.setWishId(wishService.getWishByUserId(userId).stream() // wish 여부
                .filter(wish -> place.getPlaceId() == wish.getPlaceId())
                .map(Wish::getWishId)
                .findFirst().orElse(0));

        return place;
    }
}
