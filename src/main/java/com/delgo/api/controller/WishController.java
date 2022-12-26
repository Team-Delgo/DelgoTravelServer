package com.delgo.api.controller;

import com.delgo.api.comm.CommController;
import com.delgo.api.comm.exception.ApiCode;
import com.delgo.api.dto.wish.WishReqDTO;
import com.delgo.api.service.PlaceService;
import com.delgo.api.service.WishService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/wish")
public class WishController extends CommController {

    private final WishService wishService;
    private final PlaceService placeService;

    @PostMapping
    public ResponseEntity<?> insertWish(@Validated @RequestBody WishReqDTO reqDTO) {
        return SuccessReturn(wishService.register(reqDTO.toEntity()));
    }

    @GetMapping
    public ResponseEntity getWish(@RequestParam Integer userId) {
        return SuccessReturn(wishService.getWishByUserId(userId).stream()
                .map(wish -> placeService.getPlaceById(wish.getPlaceId()).setWishId(wish.getWishId()))
                .collect(Collectors.toList()));
    }

    @DeleteMapping("/{wishId}")
    public ResponseEntity deleteWish(@PathVariable Integer wishId) {
        return (wishService.deleteWishData(wishId) == 1) ? SuccessReturn() : ErrorReturn(ApiCode.DB_DELETE_ERROR);
    }
}
