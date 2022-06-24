package com.delgo.api.controller;

import com.delgo.api.comm.CommController;
import com.delgo.api.comm.exception.ApiCode;
import com.delgo.api.domain.Place;
import com.delgo.api.domain.Wish;
import com.delgo.api.dto.wish.DeleteWishDTO;
import com.delgo.api.dto.wish.WishDTO;
import com.delgo.api.service.PlaceService;
import com.delgo.api.service.UserService;
import com.delgo.api.service.WishService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/wish")
public class WishController extends CommController {

    private final WishService wishService;
    private final UserService userService;
    private final PlaceService placeService;

    // wishlist 등록하는 api
    @PostMapping("/insert")
    public ResponseEntity<?> insertWishData(@Validated @RequestBody WishDTO wishDTO) {
        // Validate UserId ( Service 단에서 Check )
        userService.getUserByUserId(wishDTO.getUserId());

        Wish wish = wishService.insertWishData(
                Wish.builder()
                        .userId(wishDTO.getUserId())
                        .placeId(wishDTO.getPlaceId())
                        .build());
        return SuccessReturn(wish);
    }

    // wishlist 삭제하는 api
    @PostMapping("/delete")
    public ResponseEntity deleteWishData(@Validated @RequestBody DeleteWishDTO dto) {
        return (wishService.deleteWishData(dto.getWishId()) == 1) ? SuccessReturn() : ErrorReturn(ApiCode.DB_DELETE_ERROR);
    }

    // wishlist 조회 및 반환하는 api
    // 등록시간 기준으로 정렬
    @GetMapping("/select")
    public ResponseEntity selectWishData(@RequestParam Integer userId) {
        List<Wish> wishList = wishService.getWishListByUserId(userId);
        List<Place> placeList = new ArrayList<>();
        wishList.forEach(wish -> {
            Place place = placeService.getPlaceByPlaceId(wish.getPlaceId());
            place.setWishId(wish.getWishId()); // wishId 설정
            placeService.setMainPhoto(place); // mainPhotoUrl 설정
            placeList.add(place);
        });

        return SuccessReturn(placeList);
    }
}
