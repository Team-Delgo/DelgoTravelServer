package com.delgo.api.service.crawling;

import com.delgo.api.comm.ncp.service.SmsService;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TestRoomService {

    private final SmsService smsService;
    private String ADMIN_PHONE_NO = "01062511583";

    @Value("${config.driverLocation}")
    String driverLocation;
    private WebDriver driver;
    private WebDriverWait webDriverWait;

    public void crawlingProcess(String url) {

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
        webDriverWait = new WebDriverWait(driver, 10);

        //브라우저 선택
        try {
            getDataList(url);
            System.out.println("***************************************************");
        } catch (InterruptedException e) {
            System.out.println("**************************에러 발생 *************************");
            String msg = "[시간] \n" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 hh시 mm분 " +
                    "ss초"))
                    + "\n  RefreshPriceJob 동작중 에러가 발생했습니다. \n" + e.getMessage();
            try {
//                smsService.sendSMS(ADMIN_PHONE_NO, msg);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            e.printStackTrace();
        }
        System.out.println("driver 종료");
        driver.quit();     //브라우저 닫기

        try {
            // 개발자에게 쿼츠 정상 동작 문자 발송
            String msg = "[시간] \n" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 hh시 mm분 ss초"))
                    + "\n RefreshPriceJob 동작 완료";
//            smsService.sendSMS(ADMIN_PHONE_NO, msg);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getDataList(String url) throws InterruptedException {
        driver.get(url);    //브라우저에서 url로 이동한다.
        Thread.sleep(2000); //브라우저 로딩될때까지 잠시 기다린다.

        // 방이름 구하기
        List<String> roomName = getRoomName();
        System.out.println("roomName : " + roomName.get(0));

        System.out.println("-------------------------------------");

        // 기준 숙박인원 , 최대 숙박인원
        List<String> roomPersonNum = getRoomPersonNum();
        String personStandard = roomPersonNum.get(1);
        int personStandardNum = Integer.parseInt(personStandard.substring(0, personStandard.length()-1));
        String personMax = roomPersonNum.get(3);
        int personMaxNum = Integer.parseInt(personMax.substring(0, personMax.length()-1));

        System.out.println("personStandardNum : " + personStandardNum);
        System.out.println("personMaxNum : " + personMaxNum);

        System.out.println("-------------------------------------");

        // Room 사진
        List<String> roomPhotoList = getRoomPhotoURL();
        for (String photo : roomPhotoList) {
            System.out.println("photo url : " + photo);
            System.out.println("****************************");
        }

        System.out.println("-------------------------------------");

        List<String> roomNoticeList = getRoomNotice();
        for (String notice : roomNoticeList) {
            System.out.println("notice : " + notice);
            System.out.println("****************************");
        }

    }

    // 방 이름 조회
    private List<String> getRoomName() {
        List<String> roomName = new ArrayList<String>();
        List<WebElement> elements = driver.findElements(By.cssSelector(".service_info_tit"));
//        List<WebElement> elements = driver.findElements(By.cssSelector("div.figure_pagination"));
        elements.forEach(element -> roomName.add(element.getText()));

        return roomName;
    }

    // 방 이름 조회
    private List<String> getRoomPersonNum() {
        List<String> roomPersonNum = new ArrayList<String>();
        List<WebElement> elements = driver.findElements(By.cssSelector(".in_tit .personnel span"));
//        List<WebElement> elements = driver.findElements(By.cssSelector("div.figure_pagination"));
        elements.forEach(element -> roomPersonNum.add(element.getText()));

        return roomPersonNum;
    }


    // 다음 페이지로 이동
    private void nextBtnClick() throws InterruptedException {
        // 다음[ < ] 버튼
        List<WebElement> elements = driver.findElements(By.cssSelector(".btn_nxt"));
        elements.forEach(WebElement::click);

        Thread.sleep(2000); //브라우저 로딩될때까지 잠시 기다린다.
    }

    // 방 URL 조회
    private List<String> getRoomURL() {
        List<String> roomList = new ArrayList<String>();
        List<WebElement> elements = driver.findElements(By.cssSelector(".lst_item_box li.item a"));
        elements.forEach(element -> roomList.add(element.getAttribute("href")));

        return roomList;
    }

    // PlacePhoto 조회
    private List<String> getRoomPhotoURL() {
        List<String> roomPhotoList = new ArrayList<String>();
        List<WebElement> elements = driver.findElements(By.cssSelector(".visual_img li.item img"));
        elements.forEach(element -> roomPhotoList.add(element.getAttribute("src")));

        return roomPhotoList;
    }

    // 더보기 클릭
    private void moreTextBtnClick() throws InterruptedException {
        // 다음[ > ] 버튼
        List<WebElement> elements = driver.findElements(By.cssSelector("._site_desc_more_view_btn"));
        elements.forEach(WebElement::click);

        Thread.sleep(2000); //브라우저 로딩될때까지 잠시 기다린다.
    }

    // PlaceNotice 조회
    private List<String> getRoomNotice() {
        List<String> roomNoticeList = new ArrayList<String>();
        List<WebElement> elements = driver.findElements(By.cssSelector(".item_details p.dsc"));
        elements.forEach(element -> roomNoticeList.add(element.getText()));

        return roomNoticeList;
    }
}
