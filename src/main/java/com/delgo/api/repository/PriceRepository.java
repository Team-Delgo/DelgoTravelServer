package com.delgo.api.repository;

import com.delgo.api.domain.price.Price;
import com.delgo.api.domain.price.PriceId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PriceRepository extends JpaRepository<Price, PriceId> {
    List<Price> findByPriceDate(String priceDate);

    List<Price> findByPriceDateBetween(String startDate, String endDate);

    List<Price> findByPlaceId(int placeId);

    List<Price> findByRoomId(int roomId);

    List<Price> findByRoomIdAndIsBookingAndIsWaitAndPriceDateBetween(int roomId, int isBooking,int isWait, String startDate, String endDate);

    List<Price> findByRoomIdAndPriceDateBetween(int roomId, String startDate, String endDate);
}
