package com.delgo.api.service.crawling.room;

import com.delgo.api.domain.room.Room;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class GetRoomCrawlingService {

    @Value("${config.driverLocation}")
    String driverLocation;
    private WebDriver driver;

    public Room crawlingProcess(int placeId, String url) {

        System.setProperty("webdriver.chrome.whitelistedIps", "");
        System.setProperty("webdriver.chrome.driver", driverLocation); // Local

        //크롬 드라이버 셋팅 (드라이버 설치한 경로 입력)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-popup-blocking");       //팝업안띄움
        options.addArguments("--disable-gpu");            //gpu 비활성화
        options.addArguments("--blink-settings=imagesEnabled=false"); //이미지 다운 안받음
        options.addArguments("--headless");
        options.addArguments("--no-sandbox"); // linux 용
        options.addArguments("--disable-dev-shm-usage"); //linux 용

        driver = new ChromeDriver(options);

        Room room = new Room();
        //브라우저 선택
        try {
            room = getRoomData(placeId, url);
        } catch (InterruptedException e) {
            System.out.println("**************************에러 발생 *************************");
            e.printStackTrace();
        }
        System.out.println("driver 종료");
        driver.quit();     //브라우저 닫기

        return room;
    }

    private Room getRoomData(int placeId, String url) throws InterruptedException {
        driver.get(url);    //브라우저에서 url로 이동한다.
        Thread.sleep(5000); //브라우저 로딩될때까지 잠시 기다린다.

        // Room Name
        String roomName = getRoomName();
        System.out.println("roomName : " + roomName);

        // 기준 숙박인원 , 최대 숙박인원
        List<String> roomPersonNum = getRoomPersonNum();
        String personStandard = roomPersonNum.get(1);
        int personStandardNum = Integer.parseInt(personStandard.substring(0, personStandard.length()-1));
        String personMax = roomPersonNum.get(3);
        int personMaxNum = Integer.parseInt(personMax.substring(0, personMax.length()-1));

        System.out.println("personStandardNum : " + personStandardNum);
        System.out.println("personMaxNum : " + personMaxNum);

        return Room.builder()
                .placeId(placeId)
                .name(roomName)
                .personMaxNum(personMaxNum)
                .personStandardNum(personStandardNum)
                .crawlingUrl(url)
                .build();
    }

    // 방 이름 조회
    private String getRoomName() {
        List<String> roomName = new ArrayList<String>();
        List<WebElement> elements = driver.findElements(By.cssSelector(".service_info_tit"));
        elements.forEach(element -> roomName.add(element.getText()));

        return roomName.get(0);
    }

    // 방 이름 조회
    private List<String> getRoomPersonNum() {
        List<String> roomPersonNum = new ArrayList<String>();
        List<WebElement> elements = driver.findElements(By.cssSelector(".in_tit .personnel span"));
//        List<WebElement> elements = driver.findElements(By.cssSelector("div.figure_pagination"));
        elements.forEach(element -> roomPersonNum.add(element.getText()));

        return roomPersonNum;
    }
}
