package com.delgo.api.controller;

import com.delgo.api.domain.Place;
import com.delgo.api.domain.Wish;
import com.delgo.api.dto.WishDTO;
import com.delgo.api.dto.common.ResponseDTO;
import com.delgo.api.service.PlaceService;
import com.delgo.api.service.WishService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/wish")
public class WishController {

    private final WishService wishService;
    private final PlaceService placeService;

    // TODO: wishlist 등록하는 api
    @PostMapping("/insert")
    public ResponseEntity<?> insertWishData(@RequestBody Optional<WishDTO> wishDTO) {
        try { // Param Empty Check
            WishDTO checkedWishDTO = wishDTO.orElseThrow(() -> new NullPointerException("Param Empty"));
            wishService.insertWishData(checkedWishDTO.getWish());
            return ResponseEntity.ok().body(ResponseDTO.builder().code(200).codeMsg("wish insert success").build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ResponseDTO.builder().code(303).codeMsg(e.getMessage()).build());
        }
    }

    // TODO: wishlist 삭제하는 api
    @PostMapping("/delete")
    public ResponseEntity deleteWishData(@RequestBody Optional<WishDTO> wishDTO) {
        try { // Param Empty Check
            WishDTO checkedWishDTO = wishDTO.orElseThrow(() -> new NullPointerException("Param Empty"));
            int result = wishService.deleteWishData(checkedWishDTO.getWishId());
            if (result == 1)
                return ResponseEntity.ok().body(
                        ResponseDTO.builder().code(200).codeMsg("wish delete success").build());
            else
                return ResponseEntity.badRequest().body(
                        ResponseDTO.builder().code(303).codeMsg("wish delete fail").build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ResponseDTO.builder().code(303).codeMsg(e.getMessage()).build());
        }
    }

    // TODO: wishlist 조회 및 반환하는 api
    // TODO: 등록시간 기준으로 정렬해야 해서 보내줘야 함.
    @GetMapping("/select")
    public ResponseEntity selectWishData(int userId) {
        List<Wish> wishList = wishService.getWishList(userId);
        List<Place> placeList = new ArrayList<Place>();
        for (int i = 0; i < wishList.size(); i++) {
            Place place = placeService.findByUserId(wishList.get(i).getPlaceId());
            placeList.add(place);
        }
        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(200).codeMsg("Success").data(placeList).build()
        );
    }
}
