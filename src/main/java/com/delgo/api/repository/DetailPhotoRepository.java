package com.delgo.api.repository;

import com.delgo.api.domain.photo.DetailPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DetailPhotoRepository extends JpaRepository<DetailPhoto, Integer> {
    // Main 사진 조회
    Optional<DetailPhoto> findByPlaceIdAndIsMain(int placeId, int isMain);

    List<DetailPhoto> findByPlaceId(int placeId);
}
