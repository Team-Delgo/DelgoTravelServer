package com.delgo.place.service;

import com.delgo.api.domain.place.Place;
import com.delgo.api.repository.DetailPhotoRepository;
import com.delgo.api.repository.PlaceRepository;
import com.delgo.api.repository.PriceRepository;
import com.delgo.api.repository.RoomRepository;
import com.delgo.api.service.PlaceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class GetSearchDataTest {
    @Autowired
    PlaceService placeService;
    @Mock
    PlaceRepository placeRepository;
    @Mock
    RoomRepository roomRepository;
    @Mock
    PriceRepository priceRepository;
    @Mock
    DetailPhotoRepository detailPhotoRepository;

    @Nested
    @DisplayName("검색변수로 Place 조회")
    class GetSearchData {
        @Nested
        @DisplayName("정상 케이스")
        class SuccessCase {
            @Test
            @DisplayName("Name, address 검색 조건 있는 경우")
            void success1() {
                //given
                Map<String, Object> searchKeys = new HashMap<>();
                searchKeys.put("name", "엘페로");
                searchKeys.put("address", "강원도");


                List<Place> placeList = placeService.getSearchData(searchKeys);
                placeList.forEach(place -> System.out.println(place.toString()));
                // then
                assertEquals(placeList.size() >= 1, true);
            }
            @Test
            @DisplayName("Name 검색 조건 있는 경우")
            void success2() {
                //given
                Map<String, Object> searchKeys = new HashMap<>();
                searchKeys.put("name", "엘페로");
                searchKeys.put("address", "강원도");


                List<Place> placeList = placeService.getSearchData(searchKeys);
                placeList.forEach(place -> System.out.println(place.toString()));
                // then
                assertEquals(placeList.size() >= 1, true);
            }

            @Test
            @DisplayName("Address 검색 조건 있는 경우")
            void success3() {
                //given
                Map<String, Object> searchKeys = new HashMap<>();
                searchKeys.put("name", "엘페로");
                searchKeys.put("address", "강원도");


                List<Place> placeList = placeService.getSearchData(searchKeys);
                placeList.forEach(place -> System.out.println(place.toString()));
                // then
                assertEquals(placeList.size() >= 1, true);
            }

            @Test
            @DisplayName("name, Address 검색 조건 둘다 없는 경우")
            void success4() {
                //given
                Map<String, Object> searchKeys = new HashMap<>();

                List<Place> placeList = placeService.getSearchData(searchKeys);
                placeList.forEach(place -> System.out.println(place.toString()));
                // then
                assertEquals(placeList.size() >= 1, true);
            }
        }

        @Nested
        @DisplayName("비정상 케이스")
        class FailCase {
            @Test
            @DisplayName("입력된 placeId를 가진 Place가 없는 경우")
            void fail() {
//                // given
//                Optional<Place> emptyOptional = Optional.empty();
//
//                // when
//                when(placeRepository.findByPlaceId(any(Integer.class))).thenReturn(emptyOptional);
//
//                PlaceService placeService = new PlaceService(placeRepository, roomRepository, priceRepository, detailPhotoRepository);
//                Optional<Place> result = placeService.getPlaceByPlaceId(placeId);
//
//                // then
//                assertEquals(result.isPresent(), false);
            }
        }
    }
}
