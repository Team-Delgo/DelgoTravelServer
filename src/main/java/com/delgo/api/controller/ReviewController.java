package com.delgo.api.controller;

import com.delgo.api.comm.CommController;
import com.delgo.api.comm.exception.ApiCode;
import com.delgo.api.domain.Review;
import com.delgo.api.domain.pet.Pet;
import com.delgo.api.dto.ReviewDTO;
import com.delgo.api.dto.SignUpDTO;
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
    public ResponseEntity writeReview(@Validated @RequestBody ReviewDTO dto) {
        //중복 확인
        Boolean isDuplicated = reviewService.checkDuplicateReview(dto.getUserId(), dto.getPlaceId(), dto.getRoomId());
        if (isDuplicated) return ErrorReturn(ApiCode.REVIEW_DUPLICATE_ERROR);

        Review review = Review.builder()
                .userId(dto.getUserId())
                .placeId(dto.getPlaceId())
                .roomId(dto.getRoomId())
                .rating(dto.getRating())
                .text(dto.getText())
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
    public ResponseEntity updateReview(@Validated @RequestBody ReviewDTO reviewDTO) {
        try{
            int reviewId = reviewDTO.getReviewId();
            if(!reviewService.isReviewExisting(reviewId)){
                return ErrorReturn(ApiCode.REVIEW_NOT_EXIST);
            }
            Review originReview = reviewService.getReviewDataByReview(reviewId);

            if(reviewDTO.getRating() != 0){
                originReview.setRating(reviewDTO.getRating());
            }
            if(reviewDTO.getText() != null)
                originReview.setText(reviewDTO.getText());

            reviewService.insertReview(originReview);

            return SuccessReturn();

        } catch(Exception e){
            return ErrorReturn(ApiCode.UNKNOWN_ERROR);
        }
    }

    // Delete
    @PostMapping("/deleteReview")
    public ResponseEntity deleteReview(@Validated @RequestBody ReviewDTO reviewDTO) {
        try{
            reviewService.deleteReviewData(reviewDTO.getReviewId());

            return SuccessReturn();
        }  catch (Exception e){
            return ErrorReturn(ApiCode.UNKNOWN_ERROR);
        }
    }
}
