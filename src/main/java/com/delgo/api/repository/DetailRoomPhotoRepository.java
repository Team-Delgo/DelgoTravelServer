package com.delgo.api.repository;

import com.delgo.api.domain.photo.DetailRoomPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DetailRoomPhotoRepository extends JpaRepository<DetailRoomPhoto, Integer> {
    // Main 사진 조회
    Optional<DetailRoomPhoto> findByRoomIdAndIsMain(int roomId, boolean isMain);

    // Main 사진 조회
    List<DetailRoomPhoto> findByRoomId(int roomId);
}
