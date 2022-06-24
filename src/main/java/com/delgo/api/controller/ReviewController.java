package com.delgo.api.controller;

import com.delgo.api.comm.CommController;
import com.delgo.api.comm.exception.ApiCode;
import com.delgo.api.domain.Review;
import com.delgo.api.dto.ReviewDTO;
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

        Review writtenReview = reviewService.insertOrUpdateReview(review);
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

    // TODO: Update 생각해봐야 함.
//    @PostMapping("/update")
//    public ResponseEntity updateReview(@RequestBody UpdateReviewDTO dto) {
//        Optional<Review> review = reviewService.getReviewDataByReview(dto.getReviewId());
//        if(!review.isPresent())
//            return ResponseEntity.ok().body(
//                    ResponseDTO.builder().code(303).codeMsg("Review is not exist").build());
//
//
//        Review writtenReview = reviewService.insertOrUpdateReview(review);
//
//        return ResponseEntity.ok().body(
//                ResponseDTO.builder().code(200).codeMsg("Success").build());
//    }

//    // TODO: Delete
//    @PostMapping("/delete")
//    public ResponseEntity deleteReview(@RequestParam Integer reviewId) {
//        if (!reviewId.isPresent())
//            return ResponseEntity.ok().body(
//                    ResponseDTO.builder().code(303).codeMsg("Param Error").build());
//
//        int result = reviewService.deleteReviewData(reviewId.get());
//        if (result == 0)
//            return ResponseEntity.ok().body(
//                    ResponseDTO.builder().code(303).codeMsg("fail").build());
//
//        return ResponseEntity.ok().body(
//                ResponseDTO.builder().code(200).codeMsg("Success").build());
//    }
}
