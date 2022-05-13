package com.delgo.api.repository;

import com.delgo.api.domain.price.Price;
import com.delgo.api.domain.price.PriceId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PriceRepository extends JpaRepository<Price, PriceId> {
    List<Price> findByPriceDate(String priceDate);
//    int deleteByPriceDate(String date);
}
