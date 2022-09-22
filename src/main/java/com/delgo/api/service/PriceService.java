package com.delgo.api.service;

import com.delgo.api.comm.CommService;
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
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PriceService extends CommService {

    private final PriceRepository priceRepository;

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
                priceRepository.saveAll(list);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        driver.close();    //탭 닫기
        driver.quit();     //브라우저 닫기
    }

    private List<Price> getDataList(int roomId, int placeId, String url) throws InterruptedException {
        LocalDate today = LocalDate.now();
        LocalDate lastDay = today.plusMonths(2);
        List<Price> resultList = new ArrayList<>();

        driver.get(url);    //브라우저에서 url로 이동한다.
        Thread.sleep(2000);
//        webDriverWait.until(  //cssSelector로 선택한 부분이 존재할때까지 기다린다. 최대 10초
//                ExpectedConditions.presenceOfElementLocated(By.cssSelector(".list_calendar_info li.item"))
//        );

        // 모든 날짜 예약 완료시 Naver Booking에서 다음달로 이동시킨다. ( 따라서 오늘이 포함된 달로 다시 Back 시켜야 함. )
        while (true) {
            if (setInitDate(today.getYear(), today.getMonthValue()))
                break;
            backBtnClick();
        }

//        log.info("----------------------------------------------------------------");
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
                    if ((localDate.isAfter(today) && localDate.isBefore(lastDay)) || localDate.isEqual(today) ||
                            localDate.isEqual(lastDay))
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
                if ((localDate.isAfter(today) && localDate.isBefore(lastDay)) || localDate.isEqual(today) ||
                        localDate.isEqual(lastDay))
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
    private boolean setInitDate(int nowYear, int nowMonth) throws InterruptedException {
        List<String> date = new ArrayList<String>();
        List<WebElement> elements = driver.findElements(By.cssSelector(".calendar-title span"));
        elements.forEach(element -> date.add(element.getText()));

        int year = Integer.parseInt(date.get(0));
        int month = Integer.parseInt(date.get(1));

//        log.info("now date : {} , {}",nowYear,nowMonth);
//        log.info("init date : {} , {}",year,month);

        return year == nowYear && month == nowMonth;
    }

    // 다음 달로 이동
    private void nextBtnClick() throws InterruptedException {
        // 다음[ > ] 버튼
        List<WebElement> elements = driver.findElements(By.cssSelector(".fn-forward2"));
        elements.forEach(element -> element.click());

        Thread.sleep(1000); //브라우저 로딩될때까지 잠시 기다린다.
    }

    // 이전 달로 이동
    private void backBtnClick() throws InterruptedException {
        // 다음[ < ] 버튼
        List<WebElement> elements = driver.findElements(By.cssSelector(".fn-backward2"));
        elements.forEach(element -> element.click());

        Thread.sleep(1000); //브라우저 로딩될때까지 잠시 기다린다.
    }

    // 가격 Type 조회
    private List<String> getPriceType() throws InterruptedException {
        List<String> typeList = new ArrayList<String>();
        List<WebElement> elements = driver.findElements(By.cssSelector(".list_calendar_info li.item"));
        elements.forEach(element -> typeList.add(element.getText()));

//        typeList.forEach(type -> System.out.println("type: " + type));
//        System.out.println("Price Type length" + typeList.size());

        return typeList;
    }

    // 예약 가능한 날짜 가격별로 조회
    private List<String> getCanBookingList(int num) throws InterruptedException {
        List<String> list = new ArrayList<String>();
        List<WebElement> elements = driver.findElements(By.cssSelector(".tb_body .color1" + num));
        elements.forEach(element -> list.add(element.getAttribute("data-tst_cal_datetext")));

        return list;
    }

    // 예약 불가능한 날짜 조회
    private List<String> getCanNotBookingList() throws InterruptedException {
        List<String> list = new ArrayList<String>();
        List<WebElement> elements = driver.findElements(By.cssSelector(".tb_body .calendar-unselectable"));
        elements.forEach(element -> list.add(element.getAttribute("data-tst_cal_datetext")));

        return list;
    }

    public void deleteYesterdayPrice(String yesterday) {
        List<Price> deleteList = priceRepository.findByPriceDate(yesterday);
        priceRepository.deleteAll(deleteList);
    }

    // Booking getData에서 사용
    public int getOriginalPrice(int roomId, LocalDate startDt, LocalDate endDt) {
        List<Price> priceList = priceRepository.findByRoomIdAndPriceDateBetween(roomId, startDt.toString(), endDt.toString());
        if(priceList.size()>=2)
            priceList.remove(priceList.size() - 1);
        int originalPrice = 0;
        for (Price price : priceList) {
            originalPrice += formatPriceToInt(price.getPrice());
        }

        return originalPrice;
    }

    public void changeToReserveWait(LocalDate startDt, LocalDate endDt, int isWait) {
        List<Price> priceList = priceRepository.findByPriceDateBetween(startDt.toString(), endDt.toString());
        priceList.forEach(price -> {
            price.setIsWait(isWait);
            priceRepository.save(price);
        });
    }

    public void changeToReserveFix(LocalDate startDt, LocalDate endDt, int isBooking) {
        List<Price> priceList = priceRepository.findByPriceDateBetween(startDt.toString(), endDt.toString());
        priceList.forEach(price -> {
            price.setIsBooking(isBooking);
            priceRepository.save(price);
        });
    }
}
