package com.delgo;

import com.delgo.api.service.crawling.place.GetRoomListCrawlingService;
import com.delgo.api.service.crawling.TestRoomService;
import com.delgo.api.service.crawling.TestService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class QuartzServiceTest {

    @Autowired
    private TestService testService;
    @Autowired
    private TestRoomService testRoomService;

    @Autowired
    private GetRoomListCrawlingService getRoomListCrawlingService;

    @Test
    public void getPlaceCrawlingTest() {
        //given
        String url = "https://booking.naver.com/booking/3/bizes/446148"; // 켄싱턴
//        String url = "https://booking.naver.com/booking/3/bizes/75344"; // 밸런스 독
//        String url = "https://booking.naver.com/booking/3/bizes/571552"; // 골드 펫
//        String url = "https://booking.naver.com/booking/3/bizes/530675"; // 라구스 힐
        //when
        testService.crawlingProcess(url);

        //then
        assertNotNull(1);
    }

    @Test
    public void getRoomCrawlingTest() {
        //given
        String url = "https://booking.naver.com/booking/3/bizes/446148/items/4020270"; // [댕댕이와BBQ]펫룸스튜디오16평(애견동반가능) [켄싱턴]
//        String url = "https://booking.naver.com/booking/3/bizes/446148/items/4021916"; // [댕댕이와BBQ]펫룸디럭스21평(애견동반가능) [켄싱턴]
//        String url = "https://booking.naver.com/booking/3/bizes/571552"; // 골드 펫

        //when
        testRoomService.crawlingProcess(url);

        //then
        assertNotNull(1);
    }

    @Test
    public void getRoomListCrawlingServiceTest() {
        //given
        String url = "https://booking.naver.com/booking/3/bizes/446148"; // 켄싱턴
//        String url = "https://booking.naver.com/booking/3/bizes/75344"; // 밸런스 독
//        String url = "https://booking.naver.com/booking/3/bizes/571552"; // 골드 펫
//        String url = "https://booking.naver.com/booking/3/bizes/530675"; // 라구스 힐

        //when
        getRoomListCrawlingService.crawlingProcess(url);

        //then
        assertNotNull(1);
    }
}