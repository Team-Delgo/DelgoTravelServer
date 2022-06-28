package com.delgo.api.controller;

import com.delgo.api.comm.CommController;
import com.delgo.api.comm.exception.ApiCode;
import com.delgo.api.domain.Review;
import com.delgo.api.dto.CreateReviewDTO;
import com.delgo.api.dto.UpdateReviewDTO;
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
        Boolean isDuplicated = reviewService.checkDuplicateReview(createReviewDTO.getUserId(), createReviewDTO.getPlaceId(), createReviewDTO.getRoomId());
        if (isDuplicated) return ErrorReturn(ApiCode.REVIEW_DUPLICATE_ERROR);

        Review review = Review.builder()
                .userId(createReviewDTO.getUserId())
                .placeId(createReviewDTO.getPlaceId())
                .roomId(createReviewDTO.getRoomId())
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
    @PostMapping("/updateReview")
    public ResponseEntity updateReview(@Validated @RequestBody UpdateReviewDTO updateReviewDTO) {
        try{
            int reviewId = updateReviewDTO.getReviewId();
            if(!reviewService.isReviewExisting(reviewId)){
                return ErrorReturn(ApiCode.REVIEW_NOT_EXIST);
            }
            Review originReview = reviewService.getReviewDataByReview(reviewId);

            if(updateReviewDTO.getRating() != 0){
                originReview.setRating(updateReviewDTO.getRating());
            }
            if(updateReviewDTO.getText() != null)
                originReview.setText(updateReviewDTO.getText());

            reviewService.insertReview(originReview);

            return SuccessReturn();

        } catch(Exception e){
            return ErrorReturn(ApiCode.UNKNOWN_ERROR);
        }
    }
    // Delete
    @GetMapping(value = {"/deleteReview/{reviewId}", "/deleteReview"})
    public ResponseEntity deleteReview(@PathVariable(value = "reviewId") int reviewId) {
        try{
            reviewService.deleteReviewData(reviewId);

            return SuccessReturn();
        }  catch (Exception e){
            return ErrorReturn(ApiCode.UNKNOWN_ERROR);
        }
    }
}
