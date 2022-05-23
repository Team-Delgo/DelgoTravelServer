package com.delgo.api.repository;

import com.delgo.api.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByUserId(int userId);

    List<Review> findByPlaceId(int placeId);

    Optional<Review> findByReviewId(int reviewId);

    Optional<Review> findByUserIdAndPlaceIdAndRoomId(int userId, int placeId, int roomId);

    int deleteByReviewId(int reviewId);
}
