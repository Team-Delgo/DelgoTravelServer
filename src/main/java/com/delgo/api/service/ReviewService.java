package com.delgo.api.service;

import com.delgo.api.domain.Review;
import com.delgo.api.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public Review insertOrUpdateReview(Review review) {
        return reviewRepository.save(review);
    }

    public List<Review> getReviewDataByUser(int userId) {
        return reviewRepository.findByUserId(userId);
    }

    public List<Review> getReviewDataByPlace(int placeId) {
        return reviewRepository.findByPlaceId(placeId);
    }

    public Optional<Review> getReviewDataByReview(int reviewId) {
        return reviewRepository.findByReviewId(reviewId);
    }

    public Optional<Review> checkDuplicateReview(int userId, int placeId, int roomId) {
        return reviewRepository.findByUserIdAndPlaceIdAndRoomId(userId, placeId, roomId);
    }

    @Transactional
    public int deleteReviewData(int reviewId) {
        return reviewRepository.deleteByReviewId(reviewId);
    }

}