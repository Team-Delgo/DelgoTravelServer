package com.delgo.place.service;

import com.delgo.api.domain.Place;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
//@RunWith(SpringRunner.class)
@SpringBootTest
public class GetPlaceAllTest {
    @Mock
    PlaceRepository placeRepository;
    @Mock
    RoomRepository roomRepository;
    @Mock
    PriceRepository priceRepository;
    @Mock
    DetailPhotoRepository detailPhotoRepository;


    @Nested
    @DisplayName("모든 Place Data 조회")
    class GetPlaceAll {
        @Autowired
        PlaceService placeService;

        @Nested
        @DisplayName("정상 케이스")
        class SuccessCase {
            @Test
            @DisplayName("모든 Place Data 조회")
            void success() {
                List<Place> placeList = placeService.getPlaceAll();
                placeList.forEach(place -> System.out.println(place.toString()));

                assertEquals(placeList.size() >= 1, true);
            }
        }

        @Nested
        @DisplayName("List의 개수가 0인 경우 [오류는 아님]")
        class FailCase {
            @Test
            @DisplayName("List의 개수가 0인 경우")
            void fail() {
                // given
                List<Place> placeList = new ArrayList<Place>();

                // when
                when(placeRepository.findAll()).thenReturn(placeList);

                PlaceService placeService = new PlaceService(placeRepository, roomRepository, priceRepository, detailPhotoRepository);
                List<Place> result = placeService.getPlaceAll();

                // then
                assertEquals(result.size(), 0);
            }
        }
    }
}
