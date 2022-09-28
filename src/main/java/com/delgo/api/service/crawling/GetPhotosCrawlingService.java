package com.delgo.api.service.crawling;

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
public class GetPhotosCrawlingService {

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

        List<String> detailPhotoList = new ArrayList<>();
        //브라우저 선택
        try {
            detailPhotoList = getDetailPhotoList(url);
        } catch (InterruptedException e) {
            System.out.println("**************************에러 발생 *************************");
            e.printStackTrace();
        }
        System.out.println("driver 종료");
        driver.quit();     //브라우저 닫기

        return detailPhotoList;
    }

    private List<String> getDetailPhotoList(String url) throws InterruptedException {
        driver.get(url);    //브라우저에서 url로 이동한다.
        Thread.sleep(5000); //브라우저 로딩될때까지 잠시 기다린다.

        List<String> detailPhotoList = getDetailPhotoURL();
        System.out.println("detail photo List");
        for (String photo : detailPhotoList) {
            System.out.println("photo : " + photo);
            System.out.println("****************************");
        }

        return detailPhotoList.stream().distinct().collect(Collectors.toList());
    }

    // PlacePhoto 조회
    private List<String> getDetailPhotoURL() {
        List<String> detailPhotoList = new ArrayList<String>();
        List<WebElement> elements = driver.findElements(By.cssSelector(".visual_img li.item img"));
        elements.forEach(element -> detailPhotoList.add(element.getAttribute("src")));

        return detailPhotoList;
    }
}
