package com.delgo.api.service;

import com.delgo.api.comm.CommService;
import com.delgo.api.domain.price.Price;
import com.delgo.api.repository.PriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class PriceService extends CommService {
    private final PriceRepository priceRepository;

    public void deleteYesterdayPrice(String yesterday) {
        priceRepository.deleteAll(priceRepository.findByPriceDate(yesterday));
    }

    public int getOriginalPrice(int roomId, LocalDate startDt, LocalDate endDt) {
        return priceRepository.findByRoomIdAndPriceDateBetween(roomId, startDt.toString(),
                        endDt.minusDays(1).toString())  // EX) 1박 2일 -> 1박으로 계산.
                .stream().mapToInt(price -> formatPriceToInt(price.getPrice())).sum();
    }

    public void changeToReserveWait(LocalDate startDt, LocalDate endDt, boolean isWait) {
        priceRepository.findByPriceDateBetween(startDt.toString(), endDt.toString())
                .forEach(price -> priceRepository.save(price.setIsWait(isWait)));
    }

    public void changeToReserveFix(LocalDate startDt, LocalDate endDt, boolean isBooking) {
        priceRepository.findByPriceDateBetween(startDt.toString(), endDt.toString())
                .forEach(price -> priceRepository.save(price.setIsBooking(isBooking)));
    }

    // roomId로 예약가능한 날짜 조회
    public List<Price> getCanBookingDates(int roomId, LocalDate startDt, LocalDate endDt){
        return priceRepository.findByRoomIdAndIsBookingAndIsWaitAndPriceDateBetween(roomId, 0, 0, startDt.toString(), endDt.toString());
    }

    public List<Price> getPriceByRoomId(int roomId) {
        return priceRepository.findByRoomId(roomId);
    }
}
