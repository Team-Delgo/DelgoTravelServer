package com.delgo.api.controller;


import com.delgo.api.comm.CommController;
import com.delgo.api.comm.CommService;
import com.delgo.api.comm.exception.ApiCode;
import com.delgo.api.dto.DetailResDTO;
import com.delgo.api.service.*;
import com.delgo.api.service.crawling.CrawlingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/place")
public class PlaceController extends CommController {

    private final CommService commService;
    private final RoomService roomService;
    private final PhotoService photoService;
    private final PlaceService placeService;
    private final CrawlingService crawlingService;
    private final EditorNoteService editorNoteService;

    /*
     * 새로운 Place 등록
     * Request Data : crawling Url
     * Response Data : 등록한 Place & RoomList
     */
    @PostMapping
    public ResponseEntity register(@RequestParam String crawlingUrl) {
        return SuccessReturn(crawlingService.register(crawlingUrl));
    }

    /*
     * WhereToGO Page 조회
     * Request Data : userId, startDt, endDt
     * - wish 여부 판단
     * Response Data : ALL Place List
     */
    @GetMapping("/wheretogo")
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

        // TODO: 추후 placeList 보여주는 알고리즘 추가 예정 (광고 등등..)
        return SuccessReturn(placeService.getPlaceAll(userId));
    }

    /*
     * Detail Page 조회
     * Request Data : userId, placeId, startDt, endDt
     * - userId : wish 여부 check
     * - placeId : 해당 placeId로 Place 조회 및 반환
     * - startDt, endDt : 예약 가능 여부 및 최저가격 조회에 사용
     * Response Data : placeId로 조회한 Place 반환
     */
    @GetMapping("/detail")
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

        return SuccessReturn(new DetailResDTO(
                placeService.setOption(placeService.getPlaceById(placeId), LocalDate.parse(startDt), LocalDate.parse(endDt), userId), // Detail Place 조회
                editorNoteService.isEditorNoteExist(placeId), // Editor Not 가지고 있는지 여부 체크
                placeService.getPlaceNotice(placeId),  // Detail 공지사항 데이터 조회
                roomService.selectRoomList(placeId, LocalDate.parse(startDt), LocalDate.parse(endDt)), // PlaceId로 Place에 속한 Room 조회
                photoService.getDetailPhotoList(placeId)) // Detail 상단에서 보여줄 Photo List 조회
        );
    }

    /*
     * TODO Main 숙소 추천 API ( 보완해야 함 )
     * Request Data : userId
     * - userId : wish 여부 check
     * Response Data : 추천 로직에 따라 PlaceList 반환
     */
    @GetMapping("/recommend")
    public ResponseEntity recommendPlace(@RequestParam Integer userId) {
//        Collections.shuffle(placeList);
        return SuccessReturn(placeService.getPlaceAll(userId).subList(0, 3));
    }

    /*
     * EditorNote 전체 데이터 조회 API
     * Response Data : 추천 로직에 따라 EditorNote 반환
     */
    @GetMapping("/editor/all")
    public ResponseEntity gtEditorNoteByall() {
        return SuccessReturn(editorNoteService.getThumbnail());
    }

    /*
     * 특정 Place의 EditorNote 조회 API
     * Request Data : placeId
     * Response Data : placeId에 해당하는 EditorNote 반환
     */
    @GetMapping("/editor/place")
    public ResponseEntity gtEditorNoteByPlace(@RequestParam Integer placeId) {
        return SuccessReturn(editorNoteService.getEditorNoteByPlaceId(placeId));
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
        return SuccessReturn(placeService.getSearch(searchKeys).stream()
                .peek(place -> placeService.setOption(place, LocalDate.now(), LocalDate.now().plusDays(1), userId))
                .collect(Collectors.toList()));
    }
}
