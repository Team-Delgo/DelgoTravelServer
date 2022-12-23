package com.delgo.api.service;

import com.delgo.api.comm.CommService;
import com.delgo.api.domain.price.Price;
import com.delgo.api.repository.PriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
