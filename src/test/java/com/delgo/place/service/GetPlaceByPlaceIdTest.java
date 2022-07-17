package com.delgo.place.service;

import com.delgo.api.domain.place.Place;
import com.delgo.api.repository.DetailPhotoRepository;
import com.delgo.api.repository.PlaceRepository;
import com.delgo.api.repository.PriceRepository;
import com.delgo.api.repository.RoomRepository;
import com.delgo.api.service.PlaceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class GetPlaceByPlaceIdTest {
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
    @DisplayName("placeId로 Place Data 조회")
    class GetPlaceByPlaceId {
        private int placeId;
        private String placeName;

        @BeforeEach
        void setup() {
            placeId = 1;
        }

        @Nested
        @DisplayName("정상 케이스")
        class SuccessCase {
            @Test
            @DisplayName("placeId로 Place Data 조회")
            void success() {
                Place place = placeService.getPlaceByPlaceId(placeId);
                System.out.println(place.toString());

                // then
                assertEquals(place.getPlaceId(), placeId);
            }
        }

        @Nested
        @DisplayName("비정상 케이스")
        class FailCase {
            @Test
            @DisplayName("입력된 placeId를 가진 Place가 없는 경우")
            void fail() {
                // given
                Optional<Place> emptyOptional = Optional.empty();

                // when
                when(placeRepository.findByPlaceId(any(Integer.class))).thenReturn(emptyOptional);

                PlaceService placeService = new PlaceService(placeRepository, roomRepository, priceRepository, detailPhotoRepository);
                Place result = placeService.getPlaceByPlaceId(placeId);
                Optional<Place> option = Optional.of(result);

                // then
                assertEquals(option.isPresent(), false);
            }
        }
    }
}
