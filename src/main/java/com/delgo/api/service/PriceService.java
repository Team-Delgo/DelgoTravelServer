package com.delgo.api.service;

import com.delgo.api.domain.price.Price;
import com.delgo.api.repository.PriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PriceService {

    private final PriceRepository priceRepository;

    private WebDriver driver;
    public void crawlingProcess(int roomId, int placeId, String url) {
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\admin\\Desktop\\chromedriver.exe");
//        System.setProperty("webdriver.chrome.driver", "/var/www/chrome/chromedriver");
        //크롬 드라이버 셋팅 (드라이버 설치한 경로 입력)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-popup-blocking");       //팝업안띄움
        options.addArguments("headless");                       //브라우저 안띄움
        options.addArguments("--disable-gpu");            //gpu 비활성화
        options.addArguments("--blink-settings=imagesEnabled=false"); //이미지 다운 안받음
        options.addArguments("--headless");
        options.addArguments("--no-sandbox"); // linux 용
        options.addArguments("--disable-dev-shm-usage"); //linux 용

        driver = new ChromeDriver(options);

        //브라우저 선택
        try {
            List<Price> list = getDataList(roomId, placeId, url);
            priceRepository.saveAll(list);
//            return list;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        driver.close();    //탭 닫기
        driver.quit();     //브라우저 닫기

    }

    private List<Price> getDataList(int roomId, int placeId, String url) throws InterruptedException {
        LocalDate today = LocalDate.now();
        LocalDate lastDay = today.plusMonths(2);

        List<Price> resultList = new ArrayList<>();
        driver.get(url);    //브라우저에서 url로 이동한다.
        Thread.sleep(1000); //브라우저 로딩될때까지 잠시 기다린다.

        //  두달 치 가져옴
        for (int j = 0; j < 3; j++) {
            // typeList ex : { "선택", "불가", "160,000원", "190,000원", "210,000원" }
            List<String> typeList = getPriceType(); // 가격의 종류 List
            // 예약 가능한 날짜 가격별로 조회
            for (int i = 0; i < typeList.size() - 2; i++) {
                List<String> bookingTrueList = getBookingTrueList(i);
                String price = typeList.get(i + 2);
                bookingTrueList.forEach(date -> {
                    LocalDate localDate = LocalDate.parse(date);
                    // 오늘 기준 2달까지만 DB에 저장한다.
                    if ((localDate.isAfter(today) && localDate.isBefore(lastDay)) || localDate.isEqual(today) || localDate.isEqual(lastDay)) {
                        Price pObject = new Price();
                        pObject.setPlaceId(placeId);
                        pObject.setRoomId(roomId);
                        pObject.setPriceDate(date);
                        pObject.setPrice(price);
                        resultList.add(pObject);
                    }
                });
            }

            // 예약 불가능한 날짜 조회
            List<String> bookingFalseList = getBookingFalseList();
            bookingFalseList.forEach(date -> {
                LocalDate localDate = LocalDate.parse(date);
                // 오늘 기준 2달까지만 DB에 저장한다.
                if ((localDate.isAfter(today) && localDate.isBefore(lastDay)) || localDate.isEqual(today) || localDate.isEqual(lastDay)) {
                    Price pObject = new Price();
                    pObject.setPlaceId(placeId);
                    pObject.setRoomId(roomId);
                    pObject.setPriceDate(date);
                    pObject.setPrice("0");
                    pObject.setIsBooking(1);
                    resultList.add(pObject);
                }
            });

            System.out.println("result List");
            System.out.println(resultList.toString());
            System.out.println("-------------------------------------------------------------");

            nextBtnClick();
        }
        return resultList;
    }

    // 다음달로 이동
    private void nextBtnClick() throws InterruptedException {
        // 다음[ > ] 버튼
        List<WebElement> elements = driver.findElements(By.cssSelector(".fn-forward2"));
        elements.forEach(element -> element.click());

        Thread.sleep(1000);
    }

    // 가격 Type 조회
    private List<String> getPriceType() throws InterruptedException {
        List<String> typeList = new ArrayList<String>();
        List<WebElement> elements = driver.findElements(By.cssSelector(".list_calendar_info li.item"));
        elements.forEach(element -> typeList.add(element.getText()));


        typeList.forEach(type -> System.out.println("type: " + type));
        System.out.println("Price Type length" + typeList.size());

        return typeList;
    }

    // 예약 가능한 날짜 가격별로 조회
    private List<String> getBookingTrueList(int num) throws InterruptedException {
        List<String> list = new ArrayList<String>();
        List<WebElement> elements = driver.findElements(By.cssSelector(".tb_body .color1" + num));
        elements.forEach(element -> list.add(element.getAttribute("data-tst_cal_datetext")));

        System.out.println("test :: num = " + num + " : " + list.toString());

        return list;
    }

    // 예약 불가능한 날짜 조회
    private List<String> getBookingFalseList() throws InterruptedException {
        List<String> list = new ArrayList<String>();
        List<WebElement> elements = driver.findElements(By.cssSelector(".tb_body .calendar-unselectable"));
        elements.forEach(element -> list.add(element.getAttribute("data-tst_cal_datetext")));

        System.out.println("test22 :: " + list.toString());
        return list;
    }

    public void deleteYesterdayPrice(String yesterday) {
        List<Price> deleteList = priceRepository.findByPriceDate(yesterday);
        priceRepository.deleteAll(deleteList);
    }
}
