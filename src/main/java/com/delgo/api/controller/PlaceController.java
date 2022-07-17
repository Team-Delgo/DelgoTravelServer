package com.delgo.api.controller;


import com.delgo.api.comm.CommController;
import com.delgo.api.comm.CommService;
import com.delgo.api.comm.exception.ApiCode;
import com.delgo.api.domain.place.Place;
import com.delgo.api.domain.place.PlaceNotice;
import com.delgo.api.domain.room.Room;
import com.delgo.api.domain.Wish;
import com.delgo.api.domain.photo.DetailPhoto;
import com.delgo.api.dto.DetailDTO;
import com.delgo.api.repository.PlaceNoticeRepository;
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

import java.security.Policy;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/place")
public class PlaceController extends CommController {

    private final CommService commService;
    private final PlaceService placeService;
    private final RoomService roomService;
    private final PhotoService photoService;
    private final WishService wishService;

    /**
     * WhereToGO Page 조회
     * Request Data : userId , startDt, endDt
     * - wish 여부 판단
     * Response Data : ALL Place List
     */
    @GetMapping("/selectWheretogo")
    public ResponseEntity selectWhereTogo(
            @RequestParam Integer userId,
            @RequestParam String startDt,
            @RequestParam String endDt) {
        // Validate - Blank Check; [ String 만 해주면 됨 ]
        if (startDt.isBlank() || endDt.isBlank())
            return ErrorReturn(ApiCode.PARAM_ERROR);
        // Validate - 날짜 차이가 2주 이내인가? 시작날짜가 오늘보다 같거나 큰가? 종료날짜는 만료날짜랑 같거나 작은가?
        if (!commService.checkDate(startDt, endDt))
            return ErrorReturn(ApiCode.PARAM_DATE_ERROR);

        // 전체 Place 조회 ( List )
        List<Place> placeList = placeService.getPlaceAll();
        if (placeList.size() > 0) {
            placeService.setMainPhoto(placeList); // place MainPhoto 설정
            placeService.setCanBooking(placeList, LocalDate.parse(startDt), LocalDate.parse(endDt)); // 예약가능한 Place Check
            placeService.setLowestPrice(placeList, LocalDate.parse(startDt), LocalDate.parse(endDt)); // 최저가격 계산
        }

        // userId == 0일 경우 로그인 안했다고 판단.
        if (userId != 0 && placeList.size() > 0) {
            List<Wish> wishList = wishService.getWishListByUserId(userId);
            if (wishList.size() > 0) placeService.setWishId(placeList, wishList);
        }

        // TODO: 추후 placeList 보여주는 알고리즘 추가 예정 (광고 등등..)

        return SuccessReturn(placeList);
    }

    /**
     * Detail Page 조회
     * Request Data : userId, placeId, startDt,endDt
     * - userId : wish 여부 check
     * - placeId : 해당 placeId로 Place 조회 및 반환
     * - startDt, endDt : 예약 가능 여부 및 최저가격 조회에 사용
     * Response Data : placeId로 조회한 Place 반환
     */
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
        Place place = placeService.getPlaceByPlaceId(placeId); // place 조회

        placeService.setMainPhoto(place); // set MainPhoto
        place.setLowestPrice("0원"); //Detail Page에서 사용 x.
        // wish 설정
        if (userId != 0) {
            List<Wish> wishList = wishService.getWishListByUserId(userId);
            if (wishList.size() > 0) placeService.setWishId(place, wishList);
        }

        // PlaceId로 Place에 속한 Room 조회
        List<Room> roomList = roomService.selectRoomList(placeId, LocalDate.parse(startDt), LocalDate.parse(endDt));
        if (roomList.size() == 0) // Validate
            return ErrorReturn(ApiCode.NOT_FOUND_DATA);

        List<PlaceNotice> placeNoticeList = placeService.getPlaceNotice(placeId);
        List<String[]> placeNoticeString = new ArrayList<String[]>();
        for(PlaceNotice placeNotice: placeNoticeList){
            String content = placeNotice.getContent();
            String contentArr[] = content.split("\r\n");
            placeNoticeString.add(contentArr);
        }

        // PlaceId로 Detail 상단에서 보여줄 Photo List 조회
        List<DetailPhoto> detailPhotos = photoService.getDetailPhotoList(placeId);

        return SuccessReturn(new DetailDTO(place, placeNoticeString, roomList, detailPhotos));
    }

    /*
     * WhereToGo 검색 결과 반환 (UnUsed)
     * Request Data : userId, name, address, startDt, endDt
     * - userId : wish 여부 check
     * - name, address : DB에서 일치 여부 조회 (정확히 일치 X 해당 단어 포함 0) [ 빈 값 허용 ]
     * - startDt, endDt : 예약 가능 여부 및 최저가격 조회에 사용
     * Response Data : 검색조건에 부합하는 PlaceList 반환
     */
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
        if (!name.isBlank()) searchKeys.put("name", name);
        if (!address.isBlank()) searchKeys.put("address", address);

        // Name, Address로 placeList 조회
        List<Place> placeList = placeService.getSearchData(searchKeys);

        if (placeList.size() > 0) {
            placeService.setMainPhoto(placeList); // place MainPhoto 설정
            placeService.setCanBooking(placeList, LocalDate.now(), LocalDate.now().plusDays(1)); // 예약가능한 Place Check
            placeService.setLowestPrice(placeList, LocalDate.now(), LocalDate.now().plusDays(1)); // 최저가격 계산
        }

        // userId == 0 이면 로그인 없이 조회 // userId 있을 경우 wish 여부 Check
        if (userId != 0 && placeList.size() > 0) {
            List<Wish> wishList = wishService.getWishListByUserId(userId);
            placeService.setWishId(placeList, wishList);
        }

        return SuccessReturn(placeList);
    }

    /*
     * TODO Main 숙소 추천 API ( 보완해야 함 )
     * Request Data : userId, name, address, startDt, endDt
     * - userId : wish 여부 check
     * - name, address : DB에서 일치 여부 조회 (정확히 일치 X 해당 단어 포함 0) [ 빈 값 허용 ]
     * - startDt, endDt : 예약 가능 여부 및 최저가격 조회에 사용
     * Response Data : 검색조건에 부합하는 PlaceList 반환
     */
    @GetMapping("/recommend")
    public ResponseEntity recommendPlace(@RequestParam Integer userId) {
        List<Place> placeList = placeService.getPlaceAll();
        placeService.setMainPhoto(placeList);

        // userId == 0 이면 로그인 없이 조회 // userId 있을 경우 wish 여부 Check
        if (userId != 0 && placeList.size() > 0) {
            List<Wish> wishList = wishService.getWishListByUserId(userId);
            placeService.setWishId(placeList, wishList);
        }

//        Collections.shuffle(placeList);

        List<Place> returnList = new ArrayList<>();
        for (int i = 0; i < 5; i++)
            returnList.add(placeList.get(i));

        return SuccessReturn(returnList);
    }
}
