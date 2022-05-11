package com.delgo.api.repository;

import com.delgo.api.domain.DetailPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetailPhotoRepository extends JpaRepository<DetailPhoto, Integer> {
    List<DetailPhoto> findByRoomId(int roomId);
}
