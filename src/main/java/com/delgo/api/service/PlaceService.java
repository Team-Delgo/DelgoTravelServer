package com.delgo.api.service;

import com.delgo.api.domain.Place;
import com.delgo.api.domain.price.Price;
import com.delgo.api.repository.PlaceRepository;
import com.delgo.api.repository.PriceRepository;
import com.delgo.api.repository.specification.PlaceSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final PriceRepository priceRepository;

    public List<Place> getAllPlace() {
        return placeRepository.findAll();
    }

    public Place findByPlaceId(int placeId) {
        return placeRepository.findByPlaceId(placeId)
                .orElseThrow(() -> new IllegalStateException("Not Found UserData"));
    }

    // 검색
    public List<Place> searchPlace(Map<String, Object> searchKeys) {
        return placeRepository.findAll(PlaceSpecification.searchPlace(searchKeys));
    }

    public String getLowestPrice(int placeId) {
        List<Price> list = priceRepository.findByPlaceId(placeId);

        if (list.isEmpty()) return ""; // 예외처리

        List<Integer> priceList = new ArrayList<Integer>();
        list.forEach(p -> {
            String price = p.getPrice();
            if (!price.equals("")) {
                price = price.replace(",", "");
                price = price.replace("원", "");
                priceList.add(Integer.parseInt(price));
            }
        });

        DecimalFormat df = new DecimalFormat("###,###원"); //포맷팅
        return df.format(Collections.min(priceList)); // 최소가격
    }
}
