package com.delgo.api.controller;


import com.delgo.api.domain.Place;
import com.delgo.api.domain.Room;
import com.delgo.api.domain.Wish;
import com.delgo.api.dto.DetailDTO;
import com.delgo.api.dto.common.ResponseDTO;
import com.delgo.api.service.PlaceService;
import com.delgo.api.service.RoomService;
import com.delgo.api.service.WishService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/place")
public class PlaceController {

    private final PlaceService placeService;
    private final WishService wishService;
    private final RoomService roomService;

    @GetMapping("/selectWheretogo")
    public ResponseEntity selectWhereTogo(Optional<Integer> userId) {
        if (!userId.isPresent())
            return ResponseEntity.ok().body(
                    ResponseDTO.builder().code(303).codeMsg("Param Error").build());
        // 전체 place 조회 ( List )
        List<Place> placeList = placeService.getAllPlace();

        // 최저가격 계산
        placeList.forEach(place -> {
            String lowestPrice = placeService.getLowestPrice(place.getPlaceId());
            place.setLowestPrice(lowestPrice);
        });

        // userId == 0 이면 로그인 없이 API 조회
        if (userId.get() != 0) {
            List<Wish> wishList = wishService.getWishList(userId.get());
            if (wishList.size() > 0)
                wishList.forEach(wish -> { // placeList에 wishList 등록여부 적용
                    placeList.forEach(place -> {
                        if (place.getPlaceId() == wish.getPlaceId())
                            place.setWishId(wish.getWishId());
                    });
                });
        }
        // TODO: 추후 placeList 보여주는 알고리즘 추가 예정 (광고 등등..)
        // placeList Random shuffle
        Collections.shuffle(placeList);

        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(200).codeMsg("Success").data(placeList).build());
    }

    @GetMapping("/selectDetail")
    public ResponseEntity selectDetail(Optional<Integer> userId, Optional<Integer> placeId) {
        if (!userId.isPresent() || !placeId.isPresent())
            return ResponseEntity.ok().body(
                    ResponseDTO.builder().code(303).codeMsg("Param Error").build());

        Optional<Place> place = placeService.findByPlaceId(placeId.get()); // place 조회
        if (!place.isPresent())
            return ResponseEntity.ok().body(
                    ResponseDTO.builder().code(303).codeMsg("Place isn't exist").build());

        place.get().setWishId(0);
        place.get().setLowestPrice(placeService.getLowestPrice(place.get().getPlaceId()));  // 최저가격 설정

        // wish 여부 설정
        if (userId.get() != 0) {
            List<Wish> wishList = wishService.getWishList(userId.get());
            if (wishList.size() > 0)
                wishList.forEach(wish -> {
                    if (place.get().getPlaceId() == wish.getPlaceId())
                        place.get().setWishId(wish.getWishId());
                });
        }

        List<Room> roomList = roomService.selectRoomList(place.get().getPlaceId());

        DetailDTO detailDto = new DetailDTO();
        detailDto.setPlace(place.get());
        detailDto.setRoomList(roomList);

        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(200).codeMsg("Success").data(detailDto).build()
        );
    }

    // TODO: 해당 날짜에 예약이 가능한 숙소만 선택 구현해야 함.
    @GetMapping("/search")
    public ResponseEntity search(Optional<Integer> userId, Optional<String> name, Optional<String> address, Optional<String> startDt, Optional<String> endDt) {
        if (!userId.isPresent() || !startDt.isPresent() || !endDt.isPresent())
            return ResponseEntity.ok().body(
                    ResponseDTO.builder().code(303).codeMsg("Param Error").build());

        LocalDate now = LocalDate.now(); // 오늘 날짜
        LocalDate expireDate = now.plusMonths(2); // 만료 날짜
        LocalDate startDate = LocalDate.parse(startDt.get()); // 시작 날짜
        LocalDate endDate = LocalDate.parse(endDt.get()); // 종료 날짜
        LocalDate maxDate = startDate.plusWeeks(2); // 시작 날짜 기준 최대 예약 날짜 ( 14일 )
        log.info("now :{}, expire: {}, start:{}, end: {}, max:{}", now, expireDate, startDate, endDate, maxDate);

        // Validate - 날짜 차이가 2주 이내인가? 시작날짜가 오늘보다 같거나 큰가? 종료날짜는 만료날짜랑 같거나 작은가?
        if (endDate.isAfter(maxDate) || now.isAfter(startDate) || endDate.isAfter(expireDate))
            return ResponseEntity.ok().body(
                    ResponseDTO.builder().code(303).codeMsg("Date range Error").build());

        Map<String, Object> searchKeys = new HashMap<>();
        name.ifPresent(n -> searchKeys.put("name", n));
        address.ifPresent(a -> searchKeys.put("address", a));

        // Name, Address로 placeList 조회
        List<Place> placeList = placeService.searchPlace(searchKeys);

        // 예약가능한 Place Check
        List<Place> canBookingList = new ArrayList<Place>();
        if (placeList.size() > 0)
            placeList.forEach(place -> {
                // place 예약 가능 여부 Check
                boolean isBooking = placeService.checkBooking(place.getPlaceId(), LocalDate.parse(startDt.get()), LocalDate.parse(endDt.get()));
                if (isBooking) canBookingList.add(place);
            });

        // userId == 0 이면 로그인 없이 API 조회 // userId 있을 경우 wish 여부 Check
        if (userId.get() != 0) {
            List<Wish> wishList = wishService.getWishList(userId.get());
            canBookingList.forEach(place -> {
                wishList.forEach(wish -> {
                    if (place.getPlaceId() == wish.getPlaceId()) place.setWishId(wish.getWishId());
                });
            });
        }

        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(200).codeMsg("Success").data(canBookingList).build());
    }
}