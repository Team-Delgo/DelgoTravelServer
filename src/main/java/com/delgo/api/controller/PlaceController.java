package com.delgo.api.controller;


import com.delgo.api.comm.CommController;
import com.delgo.api.comm.CommService;
import com.delgo.api.comm.exception.ApiCode;
import com.delgo.api.domain.Place;
import com.delgo.api.domain.Room;
import com.delgo.api.domain.Wish;
import com.delgo.api.domain.photo.DetailPhoto;
import com.delgo.api.dto.DetailDTO;
import com.delgo.api.service.PhotoService;
import com.delgo.api.service.PlaceService;
import com.delgo.api.service.RoomService;
import com.delgo.api.service.WishService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/place")
public class PlaceController extends CommController {

    private final PlaceService placeService;
    private final WishService wishService;
    private final RoomService roomService;
    private final CommService commService;
    private final PhotoService photoService;

    @GetMapping("/selectWheretogo")
    public ResponseEntity selectWhereTogo(@RequestParam Integer userId) {
        // 전체 Place 조회 ( List )
        List<Place> placeList = placeService.getWhereToGoData();

        // wish 여부 Check [userId == 0일 경우 로그인 안했다고 판단. ]
        // placeList에 wish 등록 여부 적용
        if (userId != 0 && placeList.size() > 0) {
            List<Wish> wishList = wishService.getWishListByUserId(userId);
            if (wishList.size() > 0)
                wishList.forEach(wish -> {
                    placeList.forEach(place -> {
                        if (place.getPlaceId() == wish.getPlaceId())
                            place.setWishId(wish.getWishId());
                    });
                });
        }

        // TODO: 추후 placeList 보여주는 알고리즘 추가 예정 (광고 등등..)

        return SuccessReturn(placeList);
    }

    @GetMapping("/selectDetail")
    public ResponseEntity selectDetail(
            @RequestParam Integer userId,
            @RequestParam Integer placeId,
            @RequestParam String startDt,
            @RequestParam String endDt) {
        // Validate - Blank Check; [ String 만 해주면 됨 ]
        if (startDt.isBlank() || endDt.isBlank())
            return ErrorReturn(ApiCode.PARAM_ERROR);
        // Validate - 날짜 차이가 2주 이내인가? 시작날짜가 오늘보다 같거나 큰가? 종료날짜는 만료날짜랑 같거나 작은가?
        if (!commService.checkDate(startDt, endDt))
            return ErrorReturn(ApiCode.PARAM_DATE_ERROR);

        // Detail Place 조회
        Optional<Place> place = placeService.findByPlaceId(placeId); // place 조회
        if (place.isEmpty()) // Validate
            return ErrorReturn(ApiCode.NOT_FOUND_SEARCH);

        place.get().setLowestPrice("0원"); //Detail Page에서 사용 x.
        // wish 설정
        if (userId != 0) {
            List<Wish> wishList = wishService.getWishListByUserId(userId);
            if (wishList.size() > 0)
                wishList.forEach(wish -> {
                    if (place.get().getPlaceId() == wish.getPlaceId()) place.get().setWishId(wish.getWishId());
                });
            else place.get().setWishId(0);
        }

        // PlaceId로 Place에 속한 Room 조회
        List<Room> roomList = roomService.selectRoomList(placeId, LocalDate.parse(startDt), LocalDate.parse(endDt));
        if (roomList.size() == 0) // Validate
            return ErrorReturn(ApiCode.NOT_FOUND_SEARCH);

        // PlaceId로 Detail 상단에서 보여줄 Photo List 조회
        List<DetailPhoto> detailPhotos = photoService.getDetailPhotoList(placeId);

        return SuccessReturn(new DetailDTO(place.get(), roomList, detailPhotos));
    }

    @GetMapping("/search")
    public ResponseEntity search(
            @RequestParam Integer userId,
            @RequestParam String name,
            @RequestParam String address,
            @RequestParam String startDt,
            @RequestParam String endDt) {
        // Validate - Blank Check; [ String 만 해주면 됨 ]
        if (startDt.isBlank() || endDt.isBlank())
            return ErrorReturn(ApiCode.PARAM_ERROR);
        // Validate - 날짜 차이가 2주 이내인가? 시작날짜가 오늘보다 같거나 큰가? 종료날짜는 만료날짜랑 같거나 작은가?
        if (!commService.checkDate(startDt, endDt))
            return ErrorReturn(ApiCode.PARAM_DATE_ERROR);

        Map<String, Object> searchKeys = new HashMap<>();
        if(!name.isBlank()) searchKeys.put("name", name);
        if(!address.isBlank()) searchKeys.put("address", address);

        // Name, Address로 placeList 조회
        List<Place> placeList = placeService.getSearchPlaceListData(searchKeys, LocalDate.parse(startDt), LocalDate.parse(endDt));

        // userId == 0 이면 로그인 없이 조회 // userId 있을 경우 wish 여부 Check
        if (userId != 0 && placeList.size() > 0) {
            List<Wish> wishList = wishService.getWishListByUserId(userId);
            placeList.forEach(place -> {
                wishList.forEach(wish -> {
                    if (place.getPlaceId() == wish.getPlaceId()) place.setWishId(wish.getWishId());
                });
            });
        }

        return SuccessReturn(placeList);
    }
}
