package com.delgo.api.service;

import com.delgo.api.domain.Review;
import com.delgo.api.domain.room.Room;
import com.delgo.api.domain.user.User;
import com.delgo.api.dto.review.ReadReviewDTO;
import com.delgo.api.dto.review.ReturnReviewDTO;
import com.delgo.api.repository.ReviewPhotoRepository;
import com.delgo.api.repository.ReviewRepository;
import com.delgo.api.repository.RoomRepository;
import com.delgo.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewPhotoRepository reviewPhotoRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    // 리뷰 존재 확인 - reviewId
    public boolean isReviewExisting(int reviewId) {
        Optional<Review> review = reviewRepository.findByReviewId(reviewId);
        return review.isPresent();
    }

    // 리뷰 존재 확인 - bookingId
    public Boolean isReviewExisting(String bookingId) {
//        Optional<Review> option  = reviewRepository.findByUserIdAndPlaceIdAndRoomId(userId, placeId, roomId);
        Optional<Review> review = reviewRepository.findByBookingId(bookingId);
        return review.isPresent();
    }

    public Review insertReview(Review review) {
        return reviewRepository.save(review);
    }

    // SET PHOTO LIST
    public void setReviewPhoto(List<Review> reviewList) {
        reviewList.forEach(review -> review.setReviewPhotoList(reviewPhotoRepository.findByReviewId(review.getReviewId())));
    }

    public List<Review> getReviewDataByUser(int userId) {
        List<Review> reviewList = reviewRepository.findByUserId(userId);
        setReviewPhoto(reviewList);
        return reviewList;
    }

    public ReturnReviewDTO getReviewDataByPlace(int placeId) {
        List<Review> reviewList = reviewRepository.findByPlaceId(placeId);
        setReviewPhoto(reviewList);

        if (reviewList.size() <= 0)
            return null;

        float ratingAvg = 0;
        List<ReadReviewDTO> readReviewDTOList = new ArrayList<>();

        for (Review review : reviewList) {
            ratingAvg += review.getRating();
            User user = userRepository.findByUserId(review.getUserId()).orElseThrow(() -> new NullPointerException());
            Room room = roomRepository.findByRoomId(review.getRoomId()).orElseThrow(() -> new NullPointerException());
            ReadReviewDTO readReviewDTO = new ReadReviewDTO(review, user.getName(), room.getName(), user.getProfile());
            readReviewDTOList.add(readReviewDTO);
        }

        ratingAvg /= reviewList.size();

        ReturnReviewDTO returnReviewDTO = new ReturnReviewDTO(readReviewDTOList, ratingAvg);

        return returnReviewDTO;
    }

    public Review getReviewDataByReview(int reviewId) {
        Review review = reviewRepository.findByReviewId(reviewId).orElseThrow(() -> new NullPointerException("NOT FOUND REVIEW"));
        review.setReviewPhotoList(reviewPhotoRepository.findByReviewId(review.getReviewId()));

        return review;
    }

    @Transactional
    public void deleteReviewData(int reviewId) {
        // review Data 삭제
        reviewRepository.deleteByReviewId(reviewId);
        // review Photo Data 삭제
        reviewPhotoRepository.deleteByReviewId(reviewId);
    }

}
