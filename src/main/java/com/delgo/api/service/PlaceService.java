package com.delgo.api.service;

import com.delgo.api.domain.Place;
import com.delgo.api.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;

    public List<Place> getAllPlace() {
        return placeRepository.findAll();
    }

    public Place findByUserId(int placeId) {
        return placeRepository.findByPlaceId(placeId)
                .orElseThrow(() -> new IllegalStateException("Not Found UserData"));
    }

}
