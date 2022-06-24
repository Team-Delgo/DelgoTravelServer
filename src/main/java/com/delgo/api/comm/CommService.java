package com.delgo.api.comm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDate;

@Slf4j
@Service
public class CommService {

    // Date Validate
    // - 날짜 차이가 2주 이내인가?
    // - 시작날짜가 오늘보다 같거나 큰가?
    // - 종료날짜는 만료날짜랑 같거나 작은가?
    // - 시작날짜가 종료날짜보다 작은가?
    public boolean checkDate(String startDt, String endDt) {
        LocalDate now = LocalDate.now(); // 오늘 날짜
        LocalDate expireDate = now.plusMonths(2); // 만료 날짜
        LocalDate startDate = LocalDate.parse(startDt); // 시작 날짜
        LocalDate endDate = LocalDate.parse(endDt); // 종료 날짜
        LocalDate maxDate = startDate.plusWeeks(2); // 시작 날짜 기준 최대 예약 날짜 ( 14일 )
//        log.info("now :{}, expire: {}, start:{}, end: {}, max:{}", now, expireDate, startDate, endDate, maxDate);

        return startDate.isBefore(endDate) && !endDate.isAfter(maxDate) && !now.isAfter(startDate) && !endDate.isAfter(expireDate);
    }

    public String formatIntToPrice(int price) {
        DecimalFormat df = new DecimalFormat("###,###원"); //포맷팅
        return df.format(price);
    }

    public int formatPriceToInt(String price) {
        price = price.replace(",", "");
        price = price.replace("원", "");

        return Integer.parseInt(price);
    }
}
