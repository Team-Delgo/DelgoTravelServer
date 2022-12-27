package com.delgo.api.repository.photo;

import com.delgo.api.domain.photo.ReviewPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewPhotoRepository extends JpaRepository<ReviewPhoto, Integer> {
    List<ReviewPhoto> findByReviewId(int reviewId);

    int deleteByReviewId(int reviewId);
}
