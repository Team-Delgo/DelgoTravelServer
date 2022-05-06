package com.delgo.api.repository;

import com.delgo.api.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Integer> {
    Optional<Place> findByPlaceId(int placeId);
}
