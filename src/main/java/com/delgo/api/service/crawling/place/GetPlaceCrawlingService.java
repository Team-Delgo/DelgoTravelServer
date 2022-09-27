package com.delgo.api.service.crawling.place;

import com.delgo.api.domain.place.Place;
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
import java.util.Iterator;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class GetPlaceCrawlingService {

    @Value("${config.driverLocation}")
    String driverLocation;
    private WebDriver driver;

    public Place crawlingProcess(String url) {

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

        Place place = new Place();
        //브라우저 선택
        try {
            place = getPlaceData(url);
        } catch (InterruptedException e) {
            System.out.println("**************************에러 발생 *************************");
            e.printStackTrace();
        }
        System.out.println("driver 종료");
        driver.quit();     //브라우저 닫기

        return place;
    }

    private Place getPlaceData(String url) throws InterruptedException {
        driver.get(url);    //브라우저에서 url로 이동한다.
        Thread.sleep(5000); //브라우저 로딩될때까지 잠시 기다린다.
//        webDriverWait.until(  //cssSelector로 선택한 부분이 존재할때까지 기다린다. 최대 10초
//                ExpectedConditions.presenceOfElementLocated(By.cssSelector(".list_calendar_info li.item"))
//        );

        // 장소명 조회
        String name = getPlaceName();
        System.out.println("name : " + name);

        // 주소 조회
        String address = getAddress();
        System.out.println("address : " + address);

        // 전화번호 조회
        String phoneNo = getPhoneNo();
        System.out.println("phoneNo : " + phoneNo);

        // 지도 이동 버튼 클릭
        mapBtnClick();

        // Tab 목록 조회 및 Iterator 생성
        Iterator<String> it = driver.getWindowHandles().iterator();
        List<String> tabList = new ArrayList<String>();
        while (it.hasNext())  // hasNext() : 데이터가 있으면 true 없으면 false
            tabList.add(it.next());

        // 2번째 탭으로 이동 [지도]
        driver.switchTo().window(tabList.get(1));

        String mapUrl = driver.getCurrentUrl();
        System.out.println("mapUrl : " + mapUrl);

        return Place.builder()
                .name(name)
                .address(address)
                .phoneNo(phoneNo)
                .checkin("15:00")
                .checkout("11:00")
                .mapUrl(mapUrl)
                .build();
    }

    // 장소명 조회
    private String getPlaceName() {
        List<String> placeName = new ArrayList<String>();
        List<WebElement> elements = driver.findElements(By.cssSelector(".store_name"));
        elements.forEach(element -> placeName.add(element.getText()));

        return placeName.get(0);
    }

    // 주소 조회
    private String getAddress() {
        List<String> address = new ArrayList<String>();
        List<WebElement> elements = driver.findElements(By.cssSelector(".addr_txt"));
        elements.forEach(element -> address.add(element.getText()));

        return address.get(0);
    }

    // 전화번호 조회
    private String getPhoneNo() {
        List<String> phoneNo = new ArrayList<String>();
        List<WebElement> elements = driver.findElements(By.cssSelector(".store_tel"));
        elements.forEach(element -> phoneNo.add(element.getText()));

        return phoneNo.get(0);
    }

    // 지도로 이동
    private void mapBtnClick() throws InterruptedException {
        List<WebElement> elements = driver.findElements(By.cssSelector(".store_location"));
        elements.forEach(WebElement::click);

        Thread.sleep(5000); //브라우저 로딩될때까지 잠시 기다린다.
    }
}
