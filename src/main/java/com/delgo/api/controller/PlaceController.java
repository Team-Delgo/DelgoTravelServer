package com.delgo.api.controller;


import com.delgo.api.domain.Place;
import com.delgo.api.domain.Wish;
import com.delgo.api.dto.common.ResponseDTO;
import com.delgo.api.service.PlaceService;
import com.delgo.api.service.WishService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/place")
public class PlaceController {

    private final PlaceService placeService;
    private final WishService wishService;

    @GetMapping("/selectAll")
    public ResponseEntity selectAllPlace(int userId) {
        // 전체 place 조회 ( List )
        List<Place> placeList = placeService.getAllPlace();
        // 가격 집어넣는 로직 ( 추후 변경예정 )
        placeList.forEach(place -> place.setLowestPrice("190,000"));

        if(userId != 0) { // userId == 0 이면 로그인 없이 API 조회
            List<Wish> wishList = wishService.getWishList(userId);
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
}