package com.delgo.api.controller;

import com.delgo.api.comm.CommController;
import com.delgo.api.comm.exception.ApiCode;
import com.delgo.api.domain.Place;
import com.delgo.api.domain.Wish;
import com.delgo.api.dto.WishDTO;
import com.delgo.api.service.PlaceService;
import com.delgo.api.service.WishService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/wish")
public class WishController extends CommController {

    private final WishService wishService;
    private final PlaceService placeService;

    // wishlist 등록하는 api
    @PostMapping("/insert")
    public ResponseEntity<?> insertWishData(@Validated @RequestBody WishDTO wishDTO) {
        Wish wish = wishService.insertWishData(
                Wish.builder()
                        .userId(wishDTO.getUserId())
                        .placeId(wishDTO.getPlaceId())
                        .build());
        return SuccessReturn(wish);
    }

    // wishlist 삭제하는 api
    @PostMapping("/delete")
    public ResponseEntity deleteWishData(@RequestParam Integer userId) {
        return (wishService.deleteWishData(userId) == 1) ? SuccessReturn() : ErrorReturn(ApiCode.DB_DELETE_ERROR);
    }

    // wishlist 조회 및 반환하는 api
    // 등록시간 기준으로 정렬
    @GetMapping("/select")
    public ResponseEntity selectWishData(@RequestParam Integer userId) {
        List<Wish> wishList = wishService.getWishListByUserId(userId);
        List<Place> placeList = new ArrayList<>();
        wishList.forEach(wish -> {
            Optional<Place> place = placeService.getPlaceByPlaceId(wish.getPlaceId());
            if (place.isPresent()) {
                place.get().setWishId(wish.getWishId());
                placeList.add(place.get());
            }
        });

        return SuccessReturn(placeList);
    }
}
