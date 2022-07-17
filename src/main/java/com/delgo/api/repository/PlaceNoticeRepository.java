package com.delgo.api.repository;

import com.delgo.api.domain.place.PlaceNotice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PlaceNoticeRepository extends JpaRepository<PlaceNotice, Integer>, JpaSpecificationExecutor<PlaceNotice> {
    List<PlaceNotice> findByPlaceId(int placeId);
}