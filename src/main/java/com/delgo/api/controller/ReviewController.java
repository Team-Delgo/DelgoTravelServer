package com.delgo.api.controller;

import com.delgo.api.comm.CommController;
import com.delgo.api.comm.exception.ApiCode;
import com.delgo.api.domain.Review;
import com.delgo.api.dto.review.CreateReviewDTO;
import com.delgo.api.dto.review.UpdateReviewDTO;
import com.delgo.api.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController extends CommController {

    private final ReviewService reviewService;

    // Create
    @PostMapping("/write")
    public ResponseEntity writeReview(@Validated @RequestBody CreateReviewDTO createReviewDTO) {
        //중복 확인
//        Boolean isDuplicated = reviewService.checkDuplicateReview(createReviewDTO.getUserId(), createReviewDTO.getPlaceId(), createReviewDTO.getRoomId());
        if (reviewService.isReviewExisting(createReviewDTO.getBookingId()))
            return ErrorReturn(ApiCode.REVIEW_DUPLICATE_ERROR);

        Review review = Review.builder()
                .userId(createReviewDTO.getUserId())
                .placeId(createReviewDTO.getPlaceId())
                .roomId(createReviewDTO.getRoomId())
                .bookingId(createReviewDTO.getBookingId())
                .rating(createReviewDTO.getRating())
                .text(createReviewDTO.getText())
                .build();

        Review writtenReview = reviewService.insertReview(review);
        return SuccessReturn(writtenReview);
    }

    @GetMapping("/getReview/user")
    public ResponseEntity getReviewByUser(@RequestParam Integer userId) {
        return SuccessReturn(reviewService.getReviewDataByUser(userId));
    }

    @GetMapping("/getReview/place")
    public ResponseEntity getReviewByPlace(@RequestParam Integer placeId) {
        return SuccessReturn(reviewService.getReviewDataByPlace(placeId));
    }

    // Update
    @PostMapping("/update")
    public ResponseEntity updateReview(@Validated @RequestBody UpdateReviewDTO updateReviewDTO) {
        int reviewId = updateReviewDTO.getReviewId();
        if (!reviewService.isReviewExisting(reviewId)) {
            return ErrorReturn(ApiCode.REVIEW_NOT_EXIST);
        }
        Review originReview = reviewService.getReviewDataByReview(reviewId);

        if (updateReviewDTO.getRating() != 0) {
            originReview.setRating(updateReviewDTO.getRating());
        }
        if (updateReviewDTO.getText() != null)
            originReview.setText(updateReviewDTO.getText());

        reviewService.insertReview(originReview);

        return SuccessReturn();
    }

    // Delete
    @PostMapping(value = {"/delete/{reviewId}", "/delete"})
    public ResponseEntity deleteReview(@PathVariable(value = "reviewId") Integer reviewId) {
        if (!reviewService.isReviewExisting(reviewId)) {
            return ErrorReturn(ApiCode.REVIEW_NOT_EXIST);
        }
        reviewService.deleteReviewData(reviewId);
        return SuccessReturn();
    }
}
