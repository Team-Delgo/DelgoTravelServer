package com.delgo.api.service;

import com.delgo.api.domain.Review;
import com.delgo.api.domain.user.User;
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

    // 리뷰 존재 유무 확인
    public boolean isReviewExisting(int reviewId) {
        Optional<Review> findReview = reviewRepository.findByReviewId(reviewId);
        return findReview.isPresent();
    }

    public Review insertReview(Review review) {
        return reviewRepository.save(review);
    }

    public List<Review> getReviewDataByUser(int userId) {
        return reviewRepository.findByUserId(userId);
    }

    public List<Review> getReviewDataByPlace(int placeId) {
        return reviewRepository.findByPlaceId(placeId);
    }

    public Review getReviewDataByReview(int reviewId) {
        return reviewRepository.findByReviewId(reviewId)
                .orElseThrow(() -> new NullPointerException("NOT FOUND REVIEW"));
    }

    public Boolean checkDuplicateReview(int userId, int placeId, int roomId) {
        Optional<Review> option  = reviewRepository.findByUserIdAndPlaceIdAndRoomId(userId, placeId, roomId);
        return option.isPresent();
    }

    @Transactional
    public int deleteReviewData(int reviewId) {
        return reviewRepository.deleteByReviewId(reviewId);
    }

}
