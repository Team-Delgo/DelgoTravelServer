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
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TestService {

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
//        webDriverWait.until(  //cssSelector로 선택한 부분이 존재할때까지 기다린다. 최대 10초
//                ExpectedConditions.presenceOfElementLocated(By.cssSelector(".list_calendar_info li.item"))
//        );

        // 장소명 조회
        List<String> name = getPlaceName();
        System.out.println("name : " + name.get(0));

        // 주소 조회
        List<String> address = getAddress();
        System.out.println("address : " + address.get(0));

        // 전화번호 조회
        List<String> phoneNo = getPhoneNo();
        System.out.println("phoneNo : " + phoneNo.get(0));


        List<String> countList = getPageCount();
        System.out.println("사진 개수 :" + countList.get(0));
        System.out.println("총 Page :" + countList.get(1));

        System.out.println("-------------------------------------");

        for(int i = 0 ; i < Integer.parseInt(countList.get(1)); i++) {
            System.out.println("현재 페이지 : " + (i + 1));
            List<String> roomlist = getRoomURL().stream().distinct().collect(Collectors.toList());
            for (String room : roomlist) {
                System.out.println("room url : " + room);
                System.out.println("****************************");
            }
            nextBtnClick();
        }

        System.out.println("-------------------------------------");

        List<String> placePhotoList = getPlacePhotoURL();
        for (String photo : placePhotoList) {
            System.out.println("photo url : " + photo);
            System.out.println("****************************");
        }

        System.out.println("-------------------------------------");

        // 더보기 클릭 ( 이거 눌러야 다 나옴 )
        moreTextBtnClick();
        List<String> placeNoticeList = getPlaceNotice();
        String notice = placeNoticeList.get(3);
//        System.out.println("notice : " + notice);
        String[] testList = notice.split(" ");
        String[] testList2 = notice.split("\n");
        System.out.println("-------------------------------------");
        for(int i = 0 ; i < testList.length; i++){
            System.out.println("testList : " + testList[i]);
        }
        System.out.println("-------------------------------------");
        for(int i = 0 ; i < testList2.length; i++){
            System.out.println("testList2 : " + testList2[i]);
        }


        // 지도 이동 버튼 클릭
        mapBtnClick();

        // Tab 목록 조회
        Set<String> set = driver.getWindowHandles();
        Iterator<String> it = set.iterator();
        List<String> tabList = new ArrayList<String>();
        while (it.hasNext()) { // hasNext() : 데이터가 있으면 true 없으면 false
            tabList.add(it.next());
        }

        // 2번째 탭으로 이동 [지도]
        driver.switchTo().window(tabList.get(1));

        String mapUrl = driver.getCurrentUrl();
        System.out.println("mapUrl : " + mapUrl);

//        JavascriptExecutor js = (JavascriptExecutor) driver;
//        String secondUrl = js.executeScript("return window.location.href;").toString();
    }

    // 장소명 조회
    private List<String> getPlaceName() {
        List<String> placeName = new ArrayList<String>();
        List<WebElement> elements = driver.findElements(By.cssSelector(".store_name"));
        elements.forEach(element -> placeName.add(element.getText()));

        return placeName;
    }

    // 주소 조회
    private List<String> getAddress() {
        List<String> address = new ArrayList<String>();
        List<WebElement> elements = driver.findElements(By.cssSelector(".addr_txt"));
        elements.forEach(element -> address.add(element.getText()));

        return address;
    }

    // 전화번호 조회
    private List<String> getPhoneNo() {
        List<String> phoneNo = new ArrayList<String>();
        List<WebElement> elements = driver.findElements(By.cssSelector(".store_tel"));
        elements.forEach(element -> phoneNo.add(element.getText()));

        return phoneNo;
    }

    // 총 페이지 조회
    private List<String> getPageCount() {
        List<String> pageList = new ArrayList<String>();
        List<WebElement> elements = driver.findElements(By.cssSelector("div.figure_pagination span.off span"));
//        List<WebElement> elements = driver.findElements(By.cssSelector("div.figure_pagination"));
        elements.forEach(element -> pageList.add(element.getText()));

        return pageList;
    }

    // 지도로 이동
    private void mapBtnClick() throws InterruptedException {
        List<WebElement> elements = driver.findElements(By.cssSelector(".store_location"));
        elements.forEach(WebElement::click);

        Thread.sleep(5000); //브라우저 로딩될때까지 잠시 기다린다.
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
    private List<String> getPlacePhotoURL() {
        List<String> placePhotoList = new ArrayList<String>();
        List<WebElement> elements = driver.findElements(By.cssSelector(".visual_img li.item img"));
        elements.forEach(element -> placePhotoList.add(element.getAttribute("src")));

        return placePhotoList;
    }

    // 더보기 클릭
    private void moreTextBtnClick() throws InterruptedException {
        // 다음[ > ] 버튼
        List<WebElement> elements = driver.findElements(By.cssSelector("._site_desc_more_view_btn"));
        elements.forEach(WebElement::click);

        Thread.sleep(2000); //브라우저 로딩될때까지 잠시 기다린다.
    }

    // PlaceNotice 조회
    private List<String> getPlaceNotice() {
        List<String> placeNoticeList = new ArrayList<String>();
        List<WebElement> elements = driver.findElements(By.cssSelector(".store_details p.dsc"));
        elements.forEach(element -> placeNoticeList.add(element.getText()));

        return placeNoticeList;
    }
}
