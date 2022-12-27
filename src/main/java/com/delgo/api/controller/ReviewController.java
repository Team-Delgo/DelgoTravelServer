package com.delgo.api.controller;

import com.delgo.api.comm.CommController;
import com.delgo.api.comm.exception.ApiCode;
import com.delgo.api.dto.review.ReviewReqDTO;
import com.delgo.api.dto.review.ReviewModifyDTO;
import com.delgo.api.service.ReviewService;
import com.delgo.api.service.photo.ReviewPhotoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController extends CommController {

    private final ReviewService reviewService;
    private final ReviewPhotoService reviewPhotoService;

    @PostMapping
    public ResponseEntity registerReview(@Validated @RequestBody ReviewReqDTO reqDTO) {
        return reviewService.isReviewExisting(reqDTO.getBookingId())  //중복 확인
                ? ErrorReturn(ApiCode.REVIEW_DUPLICATE_ERROR)
                : SuccessReturn(reviewService.register(reqDTO.toEntity()));
    }

    @PutMapping
    public ResponseEntity modifyReview(@Validated @RequestBody ReviewModifyDTO modifyDTO) {
        return SuccessReturn(reviewService.modify(modifyDTO));
    }

    @GetMapping("/user")
    public ResponseEntity getReviewByUser(@RequestParam Integer userId) {
        return SuccessReturn(reviewService.getReview(userId, true));
    }

    @GetMapping("/place")
    public ResponseEntity getReviewByPlace(@RequestParam Integer placeId) {
        return SuccessReturn(Map.of(
                "reviews", reviewService.getReview(placeId, false),
                "ratingAvg", reviewService.getRatingAvg(placeId)
        ));
    }

    /*
     * 특정 리뷰 클릭시 해당 리뷰 사진 조회
     * Request Data : reviewId
     * Response Data : review 사진 리스트
     */
    @GetMapping("/photo")
    public ResponseEntity getReviewPhoto(@RequestParam Integer reviewId) {
        return SuccessReturn(reviewPhotoService.getReviewPhotos(reviewId));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity deleteReview(@PathVariable Integer reviewId) {
        if (!reviewService.isReviewExisting(reviewId))
            return ErrorReturn(ApiCode.REVIEW_NOT_EXIST);

        reviewService.delete(reviewId); // REIVEW DELETE
        reviewPhotoService.delete(reviewId); // REVIEW PHOTO DELETE

        return SuccessReturn();
    }
}
