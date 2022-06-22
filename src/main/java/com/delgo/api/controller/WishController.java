package com.delgo.api.controller;

import com.delgo.api.comm.CommController;
import com.delgo.api.comm.exception.ApiCode;
import com.delgo.api.domain.Place;
import com.delgo.api.domain.Wish;
import com.delgo.api.domain.user.User;
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
import java.util.Optional;

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
        // TODO: userId 존재하는지 체크 Validated 해야함.
        int userId = wishDTO.getUserId();
        Optional<User> user = userService.getUserByUserId(userId);
        if (user.isEmpty())
            return ErrorReturn(ApiCode.WISH_USER_NOT_EXISTING_ERROR);

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
            Optional<Place> place = placeService.getPlaceByPlaceId(wish.getPlaceId());
            if (place.isPresent()) {
                place.get().setWishId(wish.getWishId()); // wishId 설정
                placeService.setMainPhoto(place.get()); // mainPhotoUrl 설정
                placeList.add(place.get());
            }
        });

        return SuccessReturn(placeList);
    }
}
