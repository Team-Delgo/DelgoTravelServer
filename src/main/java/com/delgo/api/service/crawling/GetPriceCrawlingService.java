package com.delgo.api.service.crawling;

import com.delgo.api.comm.CommService;
import com.delgo.api.comm.ncp.service.SmsService;
import com.delgo.api.domain.price.Price;
import com.delgo.api.domain.room.Room;
import com.delgo.api.repository.PriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetPriceCrawlingService extends CommService {

    private final PriceRepository priceRepository;
    private final SmsService smsService;
    private String ADMIN_PHONE_NO = "01062511583";

    @Value("${config.driverLocation}")
    String driverLocation;
    private WebDriver driver;
    private WebDriverWait webDriverWait;

    public void crawlingProcess(List<Room> roomList) {
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
        roomList.forEach(room -> {
            try {
                List<Price> list = getDataList(room.getRoomId(), room.getPlaceId(), room.getCrawlingUrl());
                list.forEach(price -> System.out.println(price.toString()));
                System.out.println("***************************************************");
                priceRepository.saveAll(list);
            } catch (InterruptedException e) {
                System.out.println("**************************에러 발생 *************************");
                String msg = "[시간] \n" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 hh시 mm분 ss초"))
                        + "\n  RefreshPriceJob 동작중 에러가 발생했습니다. \n" + e.getMessage();
                try {
                    smsService.sendSMS(ADMIN_PHONE_NO, msg);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                e.printStackTrace();
            }
        });
        System.out.println("driver 종료");
        driver.quit();     //브라우저 닫기

        try {
            // 개발자에게 쿼츠 정상 동작 문자 발송
            String msg = "[시간] \n" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 hh시 mm분 ss초"))
                    + "\n RefreshPriceJob 동작 완료";
            smsService.sendSMS(ADMIN_PHONE_NO, msg);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Price> getDataList(int roomId, int placeId, String url) throws InterruptedException {
        LocalDate today = LocalDate.now();
        LocalDate lastDay = today.plusMonths(2);
        List<Price> resultList = new ArrayList<>();

        driver.get(url);    //브라우저에서 url로 이동한다.
        webDriverWait.until(  //cssSelector로 선택한 부분이 존재할때까지 기다린다. 최대 10초
                ExpectedConditions.presenceOfElementLocated(By.cssSelector(".list_calendar_info li.item")));

        // 모든 날짜 예약 완료시 Naver Booking에서 다음달로 이동시킨다. ( 따라서 오늘이 포함된 달로 다시 Back 시켜야 함. )
        while (true) {
            if (setInitDate(today.getYear(), today.getMonthValue())) break;
            backBtnClick();
        }

//          두달 치 가져옴
        for (int j = 0; j < 3; j++) {
            // typeList ex : { "선택", "불가", "160,000원", "190,000원", "210,000원" }
            List<String> typeList = getPriceType(); // 가격의 종류 List
            // 예약 가능한 날짜 가격별로 조회
            for (int i = 0; i < typeList.size() - 2; i++) {
                List<String> canBookingList = getCanBookingList(i);
                String price = typeList.get(i + 2);
                canBookingList.forEach(date -> {
                    LocalDate localDate = LocalDate.parse(date);
                    // 오늘 기준 2달까지만 DB에 저장한다.
                    if ((localDate.isAfter(today) && localDate.isBefore(lastDay)) || localDate.isEqual(today) || localDate.isEqual(lastDay))
                        resultList.add(
                                Price.builder()
                                        .placeId(placeId)
                                        .roomId(roomId)
                                        .priceDate(date)
                                        .price(price)
                                        .isWait(0)
                                        .build());
                });
            }

            // 예약 불가능한 날짜 조회
            List<String> canNotBookingList = getCanNotBookingList();
            canNotBookingList.forEach(date -> {
                LocalDate localDate = LocalDate.parse(date);
                // 오늘 기준 2달까지만 DB에 저장한다.
                if ((localDate.isAfter(today) && localDate.isBefore(lastDay)) || localDate.isEqual(today) || localDate.isEqual(lastDay))
                    resultList.add(
                            Price.builder()
                                    .placeId(placeId)
                                    .roomId(roomId)
                                    .priceDate(date)
                                    .price("0")
                                    .isBooking(1)
                                    .isWait(0)
                                    .build());
            });
            nextBtnClick();
        }
        return resultList;
    }

    // 크롤링 시작 날짜 조정
    private boolean setInitDate(int nowYear, int nowMonth) {
        List<String> date = new ArrayList<String>();
        List<WebElement> elements = driver.findElements(By.cssSelector(".calendar-title span"));
        elements.forEach(element -> date.add(element.getText()));

        int year = Integer.parseInt(date.get(0));
        int month = Integer.parseInt(date.get(1));

        return year == nowYear && month == nowMonth;
    }

    // 다음 달로 이동
    private void nextBtnClick() throws InterruptedException {
        // 다음[ > ] 버튼
        List<WebElement> elements = driver.findElements(By.cssSelector(".fn-forward2"));
        elements.forEach(WebElement::click);

        Thread.sleep(2000); //브라우저 로딩될때까지 잠시 기다린다.
    }

    // 이전 달로 이동
    private void backBtnClick() throws InterruptedException {
        // 다음[ < ] 버튼
        List<WebElement> elements = driver.findElements(By.cssSelector(".fn-backward2"));
        elements.forEach(WebElement::click);

        Thread.sleep(2000); //브라우저 로딩될때까지 잠시 기다린다.
    }

    // 가격 Type 조회
    private List<String> getPriceType() {
        List<String> typeList = new ArrayList<String>();
        List<WebElement> elements = driver.findElements(By.cssSelector(".list_calendar_info li.item"));
        elements.forEach(element -> typeList.add(element.getText()));

        return typeList;
    }

    // 예약 가능한 날짜 가격별로 조회
    private List<String> getCanBookingList(int num) {
        List<String> list = new ArrayList<String>();
        List<WebElement> elements = driver.findElements(By.cssSelector(".tb_body .color1" + num));
        elements.forEach(element -> list.add(element.getAttribute("data-tst_cal_datetext")));

        return list;
    }

    // 예약 불가능한 날짜 조회
    private List<String> getCanNotBookingList() {
        List<String> list = new ArrayList<String>();
        List<WebElement> elements = driver.findElements(By.cssSelector(".tb_body .calendar-unselectable"));
        elements.forEach(element -> list.add(element.getAttribute("data-tst_cal_datetext")));

        return list;
    }
}
