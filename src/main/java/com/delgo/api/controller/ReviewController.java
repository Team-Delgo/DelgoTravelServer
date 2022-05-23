package com.delgo.api.controller;

import com.delgo.api.domain.Review;
import com.delgo.api.dto.ReviewDTO;
import com.delgo.api.dto.common.ResponseDTO;
import com.delgo.api.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService;

    // Create
    // TODO: Validation
    @PostMapping("/write")
    public ResponseEntity writeReview(@RequestBody ReviewDTO dto) {
        //중복 확인
        Optional<Review> check = reviewService.checkDuplicateReview(dto.getUserId(), dto.getPlaceId(), dto.getRoomId());
        if (check.isPresent())
            return ResponseEntity.ok().body(
                    ResponseDTO.builder().code(303).codeMsg("Review is Duplicate").build());

        Review review = Review.builder()
                .userId(dto.getUserId())
                .placeId(dto.getPlaceId())
                .roomId(dto.getRoomId())
                .rating(dto.getRating())
                .text(dto.getText())
                .build();

        Review writtenReview = reviewService.insertOrUpdateReview(review);

        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(200).codeMsg("Success").data(writtenReview).build());
    }

    @GetMapping("/getReview/user")
    public ResponseEntity getReviewByUser(Optional<Integer> userId) {
        if (!userId.isPresent())
            return ResponseEntity.ok().body(
                    ResponseDTO.builder().code(303).codeMsg("Param Error").build());

        List<Review> reviewList = reviewService.getReviewDataByUser(userId.get());

        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(200).codeMsg("Success").data(reviewList).build());
    }

    @GetMapping("/getReview/place")
    public ResponseEntity getReviewByPlace(Optional<Integer> placeId) {
        if (!placeId.isPresent())
            return ResponseEntity.ok().body(
                    ResponseDTO.builder().code(303).codeMsg("Param Error").build());

        List<Review> reviewList = reviewService.getReviewDataByPlace(placeId.get());

        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(200).codeMsg("Success").data(reviewList).build());
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

    // TODO: Delete
    @PostMapping("/delete")
    public ResponseEntity deleteReview(@RequestParam Optional<Integer> reviewId) {
        if (!reviewId.isPresent())
            return ResponseEntity.ok().body(
                    ResponseDTO.builder().code(303).codeMsg("Param Error").build());

        int result = reviewService.deleteReviewData(reviewId.get());
        if (result == 0)
            return ResponseEntity.ok().body(
                    ResponseDTO.builder().code(303).codeMsg("fail").build());

        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(200).codeMsg("Success").build());
    }
}
