package com.delgo.api.controller;


import com.delgo.api.comm.CommService;
import com.delgo.api.comm.exception.ApiCode;
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
    private final CommService commService;

    @GetMapping("/selectWheretogo")
    public ResponseEntity selectWhereTogo(Optional<Integer> userId) {
        // Validate - Null Check;
        if (!userId.isPresent())
            return ResponseEntity.ok().body(
                    ResponseDTO.builder().code(303).codeMsg(ApiCode.PARAM_ERROR).build());

        // 전체 place 조회 ( List )
        List<Place> placeList = placeService.getAllPlace();
        // 예약가능한 Place Check
        if (placeList.size() > 0)
            placeList.forEach(place -> {
                // place 예약 가능 여부 Check ( 초기 페이지에서는 오늘 기준 1박으로 잡는다. )
                boolean isBooking = placeService.checkBooking(place.getPlaceId(), LocalDate.now(), LocalDate.now().plusDays(1));
                if (!isBooking) place.setIsBooking(1);
            });

        // 최저가격 계산
        if (placeList.size() > 0)
            placeList.forEach(place -> {
                if (place.getIsBooking() == 0) { // 예약가능할 경우 최저가격 계산
                    String lowestPrice = placeService.getLowestPrice(place.getPlaceId(), LocalDate.now(), LocalDate.now().plusDays(1));
                    place.setLowestPrice(lowestPrice);
                } else { // 예약 불가능할 경우 0원 입력
                    place.setLowestPrice("0원");
                }
            });

        // wish 여부 Check [userId == 0일 경우 로그인 안했다고 판단. ]
        if (userId.get() != 0 && placeList.size() > 0) {
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
                ResponseDTO.builder().code(200).codeMsg(ApiCode.SUCCESS).data(placeList).build());
    }

    @GetMapping("/selectDetail")
    public ResponseEntity selectDetail(Optional<Integer> userId, Optional<Integer> placeId, Optional<String> startDt) {
        // Validate - Null Check;
        if (!userId.isPresent() || !placeId.isPresent() || !startDt.isPresent())
            return ResponseEntity.ok().body(
                    ResponseDTO.builder().code(303).codeMsg(ApiCode.PARAM_ERROR).build());
        // Validate - Blank Check; [ String 만 해주면 됨 ]
        if (startDt.get().isEmpty())
            return ResponseEntity.ok().body(
                    ResponseDTO.builder().code(303).codeMsg(ApiCode.PARAM_ERROR).build());

        Optional<Place> place = placeService.findByPlaceId(placeId.get()); // place 조회
        if (!place.isPresent()) // Validate
            return ResponseEntity.ok().body(
                    ResponseDTO.builder().code(303).codeMsg("Place isn't exist").build());

        place.get().setLowestPrice("0원"); //Detail Page에서 사용 x.
        // wish 설정
        if (userId.get() != 0) {
            List<Wish> wishList = wishService.getWishList(userId.get());
            if (wishList.size() > 0)
                wishList.forEach(wish -> {
                    if (place.get().getPlaceId() == wish.getPlaceId()) place.get().setWishId(wish.getWishId());
                });
            else place.get().setWishId(0);
        }

        List<Room> roomList = roomService.selectRoomList(placeId.get(), startDt.get());
        if (roomList.size() == 0) // Validate
            return ResponseEntity.ok().body(
                    ResponseDTO.builder().code(200).codeMsg(ApiCode.FAIL).build());

        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(200).codeMsg(ApiCode.SUCCESS).data(new DetailDTO(place.get(), roomList)).build());
    }

    @GetMapping("/search")
    public ResponseEntity search(
            Optional<Integer> userId,
            Optional<String> name,
            Optional<String> address,
            Optional<String> startDt,
            Optional<String> endDt) {
        // Validate - Null Check;
        if (!userId.isPresent() || !startDt.isPresent() || !endDt.isPresent())
            return ResponseEntity.ok().body(
                    ResponseDTO.builder().code(303).codeMsg(ApiCode.PARAM_ERROR).build());
        // Validate - Blank Check; [ String 만 해주면 됨 ]
        if (startDt.get().isEmpty() || endDt.get().isEmpty())
            return ResponseEntity.ok().body(
                    ResponseDTO.builder().code(303).codeMsg(ApiCode.PARAM_ERROR).build());
        // Validate - 날짜 차이가 2주 이내인가? 시작날짜가 오늘보다 같거나 큰가? 종료날짜는 만료날짜랑 같거나 작은가?
        if (!commService.checkDate(startDt.get(), endDt.get()))
            return ResponseEntity.ok().body(
                    ResponseDTO.builder().code(303).codeMsg("Date range Error").build());

        Map<String, Object> searchKeys = new HashMap<>();
        name.ifPresent(n -> searchKeys.put("name", n));
        address.ifPresent(a -> searchKeys.put("address", a));

        // Name, Address로 placeList 조회
        List<Place> placeList = placeService.searchPlace(searchKeys);

        // 예약가능한 Place Check
        if (placeList.size() > 0) {
            placeList.forEach(place -> {
                // place 예약 가능 여부 Check
                boolean isBooking = placeService.checkBooking(place.getPlaceId(), LocalDate.parse(startDt.get()), LocalDate.parse(endDt.get()));
                if (isBooking) place.setIsBooking(1);
            });

            // userId == 0 이면 로그인 없이 API 조회 // userId 있을 경우 wish 여부 Check
            if (userId.get() != 0) {
                List<Wish> wishList = wishService.getWishList(userId.get());
                placeList.forEach(place -> {
                    wishList.forEach(wish -> {
                        if (place.getPlaceId() == wish.getPlaceId()) place.setWishId(wish.getWishId());
                    });
                });
            }

            // 최저가격 계산
            placeList.forEach(place -> {
                String lowestPrice = placeService.getLowestPrice(place.getPlaceId(), LocalDate.parse(startDt.get()), LocalDate.parse(endDt.get()));
                place.setLowestPrice(lowestPrice);
            });
        }

        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(200).codeMsg("Success").data(placeList).build());
    }
}
