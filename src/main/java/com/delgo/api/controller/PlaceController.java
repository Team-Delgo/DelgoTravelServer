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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/place")
public class PlaceController {

    private final PlaceService placeService;
    private final WishService wishService;
    private final RoomService roomService;

    // TODO: selectWhereTogo 로 변경예정
    @GetMapping("/selectWheretogo")
    public ResponseEntity selectWhereTogo(int userId) {
        // 전체 place 조회 ( List )
        List<Place> placeList = placeService.getAllPlace();

        // 최저가격 집어넣는 로직 ( 추후 변경예정 )
        placeList.forEach(place -> place.setLowestPrice("190,000"));

        // userId == 0 이면 로그인 없이 API 조회
        if (userId != 0) {
            List<Wish> wishList = wishService.getWishList(userId);
            if (wishList.size() > 0)
                wishList.forEach(wish -> { // placeList에 wishList 등록여부 적용
                    placeList.forEach(place -> {
                        if (place.getPlaceId() == wish.getPlaceId())
                            place.setWishId(wish.getWishId());
                    });
                });
        }
        // placeList Random shuffle
        Collections.shuffle(placeList);

        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(200).codeMsg("Success").data(placeList).build()
        );
    }

    // TODO: 해당 날짜에 예약이 가능한 숙소만 선택 구현해야 함.
    @GetMapping("/search")
    public ResponseEntity search(String name, String address, String startDt, String endDt, int userId) {
        Map<String, Object> searchKeys = new HashMap<>();
        if (name != null) searchKeys.put("name", name);
        if (address != null) searchKeys.put("address", address);

        List<Place> placeList = placeService.searchPlace(searchKeys);

        // userId == 0 이면 로그인 없이 API 조회
        if (userId != 0) {
            List<Wish> wishList = wishService.getWishList(userId);
            placeList.forEach(place -> {
                wishList.forEach(wish -> {
                    if (place.getPlaceId() == wish.getPlaceId())
                        place.setWishId(wish.getWishId());
                });
            });
        }

        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(200).codeMsg("Success").data(placeList).build()
        );
    }

    @GetMapping("/selectDetail")
    public ResponseEntity selectDetail(int userId, int placeId) {

        Place place = placeService.findByPlaceId(placeId); // place 조회
        place.setWishId(0);
        place.setLowestPrice("190,000");  // 최저 가격 설정

        // wish 여부 설정
        if (userId != 0) {
            List<Wish> wishList = wishService.getWishList(userId);
            if (wishList.size() > 0)
                wishList.forEach(wish -> {
                    if (place.getPlaceId() == wish.getPlaceId())
                        place.setWishId(wish.getWishId());
                });
        }

        // TODO: room 조회
        List<Room> roomList = roomService.selectRoomList(place.getPlaceId());

        DetailDTO detailDto = new DetailDTO();
        detailDto.setPlace(place);
        detailDto.setRoomList(roomList);

        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(200).codeMsg("Success").data(detailDto).build()
        );
    }
}