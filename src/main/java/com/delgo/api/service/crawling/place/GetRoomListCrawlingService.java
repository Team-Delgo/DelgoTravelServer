package com.delgo.api.service.crawling.place;

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
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class GetRoomListCrawlingService {

    @Value("${config.driverLocation}")
    String driverLocation;
    private WebDriver driver;

    public List<String> crawlingProcess(String url) {
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

        List<String> roomUrlList = new ArrayList<>();
        //브라우저 선택
        try {
            roomUrlList = getRoomUrlList(url);
        } catch (InterruptedException e) {
            System.out.println("**************************에러 발생 *************************");
            e.printStackTrace();
        }
        System.out.println("driver 종료");
        driver.quit();     //브라우저 닫기

        return roomUrlList;
    }

    private List<String> getRoomUrlList(String url) throws InterruptedException {
        driver.get(url);    //브라우저에서 url로 이동한다.
        Thread.sleep(5000); //브라우저 로딩될때까지 잠시 기다린다.

        List<String> returnList = new ArrayList<String>();

        int pageCount = getPageCount();
        System.out.println("총 Page :" + pageCount);

        for (int i = 0; i < pageCount; i++) {
            System.out.println("현재 페이지 : " + (i + 1));
            List<String> roomlist = getRoomURL().stream().distinct().collect(Collectors.toList());
            for (String room : roomlist) {
                System.out.println("room url : " + room);
            }
            returnList.addAll(roomlist);
            nextBtnClick();
        }

        return returnList;
    }

    // 총 페이지 조회
    private int getPageCount() {
        List<String> pageList = new ArrayList<String>();
        List<WebElement> elements = driver.findElements(By.cssSelector("div.figure_pagination span.off span"));
        elements.forEach(element -> pageList.add(element.getText()));

        return Integer.parseInt(pageList.get(1));
    }

    // 다음 페이지로 이동
    private void nextBtnClick() throws InterruptedException {
        // 다음[ < ] 버튼
        List<WebElement> elements = driver.findElements(By.cssSelector(".btn_nxt"));
        elements.forEach(WebElement::click);

        Thread.sleep(5000); //브라우저 로딩될때까지 잠시 기다린다.
    }

    // 방 URL 조회
    private List<String> getRoomURL() {
        List<String> roomList = new ArrayList<String>();
        List<WebElement> elements = driver.findElements(By.cssSelector(".lst_item_box li.item a"));
        elements.forEach(element -> roomList.add(element.getAttribute("href")));

        return roomList;
    }
}
