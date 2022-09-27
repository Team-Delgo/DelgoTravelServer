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
