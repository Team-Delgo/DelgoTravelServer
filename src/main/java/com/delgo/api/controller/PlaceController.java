package com.delgo.api.controller;


import com.delgo.api.domain.Place;
import com.delgo.api.dto.common.ResponseDTO;
import com.delgo.api.service.PlaceService;
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

    @GetMapping("/selectAll")
    public ResponseEntity selectAllPlace() {
        // 전체 place 조회 ( List )
        List<Place> placeList = placeService.getAllPlace();
        // 가격 집어넣는 로직 ( 추후 변경예정 )
        placeList.forEach(place -> place.setLowestPrice("190,000"));
        // placeList Random shuffle
        Collections.shuffle(placeList);

        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(200).codeMsg("Success").data(placeList).build()
        );
    }
}