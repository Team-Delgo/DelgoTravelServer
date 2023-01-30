package com.delgo.api.service;

import com.delgo.api.domain.Review;
import com.delgo.api.dto.review.ReviewResDTO;
import com.delgo.api.dto.review.ReviewModifyDTO;
import com.delgo.api.repository.*;
import com.delgo.api.service.photo.ReviewPhotoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    // Repository
    private final ReviewRepository reviewRepository;

    // Service
    private final UserService userService;
    private final RoomService roomService;
    private final PlaceService placeService;
    private final ReviewPhotoService reviewPhotoService;


    public Review register(Review review) {
        return reviewRepository.save(review);
    }

    public Review modify(ReviewModifyDTO modifyDTO) {
        Review review = getReviewById(modifyDTO.getReviewId());
        if (modifyDTO.getRating() != 0) review.setRating(modifyDTO.getRating());
        if (modifyDTO.getText() != null) review.setText(modifyDTO.getText());

        return reviewRepository.save(review);
    }

    public Review getReviewById(int reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NullPointerException("NOT FOUND REVIEW"))
                .setReviewPhotos(reviewPhotoService.getReviewPhotos(reviewId));
    }

    // 리뷰 존재 확인 - reviewId
    public boolean isReviewExisting(int reviewId) {
        return reviewRepository.findByReviewId(reviewId).isPresent();
    }

    // 리뷰 존재 확인 - bookingId
    public Boolean isReviewExisting(String bookingId) {
        return reviewRepository.findByBookingId(bookingId).isPresent();
    }

    public List<ReviewResDTO> getReview(int userId, boolean byUser) {
        List<Review> reviews = (byUser)
                ? reviewRepository.findByUserId(userId) // USER REVIEW
                : reviewRepository.findByPlaceId(userId); // PLACE REVIEW
        return reviews.stream().map(review -> {
                    review.setReviewPhotos(reviewPhotoService.getReviewPhotos(review.getReviewId())); // 사진 설정
                    return review.toResDTO(
                            userService.getUserById(review.getUserId()), // USER
                            placeService.getPlaceById(review.getPlaceId()), // PLACE
                            roomService.getRoomById(review.getRoomId()));}) //ROOM
                .collect(Collectors.toList());
    }

    public double getRatingAvg(int placeId){
        return reviewRepository.findByPlaceId(placeId).stream()
                .mapToInt(Review::getRating)
                .average().orElse(0);
    }

    @Transactional
    public void delete(int reviewId) {
        reviewRepository.deleteByReviewId(reviewId);
    }
}
