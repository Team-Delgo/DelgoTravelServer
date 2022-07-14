package com.delgo.api.repository;

import com.delgo.api.domain.Cancel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CancelRepository extends JpaRepository<Cancel, Integer> {
    Optional<Cancel> findByPlaceIdAndRemainDay(int placeId, int remainDay);
}
