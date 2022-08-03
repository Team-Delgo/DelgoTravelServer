package com.delgo.place;

import com.delgo.api.domain.place.Place;
import com.delgo.api.repository.PlaceRepository;
import com.delgo.api.service.PlaceService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PlaceServiceTest {

    @Autowired
    private PlaceService placeService;
    @Autowired
    private PlaceRepository placeRepository;

    @Test
    public void getPlaceAllTest() {
        //given
        //when
        List<Place> placeList = placeService.getPlaceAll();

        //then
        assertTrue(placeList.size() >= 1);
    }

    @Test
    public void getPlaceByPlaceIdTest() {
        //given
        int placeId = 2;

        //when
        Place place = placeService.getPlaceByPlaceId(placeId);

        //then
        assertEquals(place.getPlaceId(), 2);
        assertNotNull(place);
    }

//    @Test
//    public void getSearchDataTest() {
//        //given
//        Map<String, Object> searchKeys = new HashMap<>();
//        searchKeys.put("name", "");
//        searchKeys.put("address", "춘천");
//
//        //when
//        List<Place> placeList = placeService.getSearchData(searchKeys, LocalDate.now(), LocalDate.now().plusDays(1));
//        System.out.println(placeList.toString());
//
//        //then
//        assertTrue(placeList.size() >= 1);
//    }
//
//    @Test
//    public void setMainPhotoTest() {
//        //given
//        List<Place> list = placeRepository.findAll();
//
//        // photoUrl 빈 값 확인;
//        list.forEach(place -> {
//            assertTrue(place.getMainPhotoUrl() == null);
//        });
//
//        //when
//        List<Place> placeList = placeService.setMainPhoto(list);
//        System.out.println(placeList.toString());
//
//        //then
//        placeList.forEach(place -> {
//            assertTrue(!place.getMainPhotoUrl().isEmpty());
//        });
//    }
//
//    @Test
//    public void setCanBookingTest() {
//        //given
//        List<Place> list = placeRepository.findAll();
//
//        // IsBooking 모두 default = 0 인 것 확인.
//        list.forEach(place -> {
//            assertTrue(place.getIsBooking() == 0);
//        });
//
//        //when
//        List<Place> placeList = placeService.setCanBooking(list, LocalDate.now(), LocalDate.now().plusDays(1));
//        System.out.println(placeList.toString());
//
//        //then
//        // 정확하지 않음 해당 테스트는 단 한개라도 예약이 불가능한 방이 있을거라 예상.
//        // but 모두 예약이 가능할 수 잇음.
//        int count = 0;
//        for (Place place : placeList) {
//            if (place.getIsBooking() == 1)
//                count = count + 1;
//        }
//
//        assertTrue(count != 0);
//    }
}