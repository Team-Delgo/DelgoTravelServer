package com.delgo.api.service;

import com.delgo.api.domain.Place;
import com.delgo.api.domain.Review;
import com.delgo.api.domain.Room;
import com.delgo.api.domain.user.User;
import com.delgo.api.dto.review.ReadReviewDTO;
import com.delgo.api.dto.review.ReturnReviewDTO;
import com.delgo.api.repository.*;
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
    private final PlaceRepository placeRepository;

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
    private void setReviewPhoto(List<Review> reviewList) {
        reviewList.forEach(review -> review.setReviewPhotoList(reviewPhotoRepository.findByReviewId(review.getReviewId())));
    }

    // SET ReadReviewDTO List
    private List<ReadReviewDTO> setReadReviewDTO(List<Review> reviewList) {
        List<ReadReviewDTO> readReviewDTOList = new ArrayList<ReadReviewDTO>();
        for (Review review : reviewList) {
            User user = userRepository.findByUserId(review.getUserId()).orElseThrow(() -> new NullPointerException("NOT FOUND USER"));
            Place place = placeRepository.findByPlaceId(review.getPlaceId()).orElseThrow(() -> new NullPointerException("NOT FOUND PLACE"));
            Room room = roomRepository.findByRoomId(review.getRoomId()).orElseThrow(() -> new NullPointerException("NOT FOUND ROOM"));
            ReadReviewDTO readReviewDTO = new ReadReviewDTO(review, user.getName(), place.getName(), room.getName(), user.getProfile());
            readReviewDTOList.add(readReviewDTO);
        }

        return readReviewDTOList;
    }

    public List<ReadReviewDTO> getReviewDataByUser(int userId) {
        List<Review> reviewList = reviewRepository.findByUserId(userId);
        setReviewPhoto(reviewList);

        return setReadReviewDTO(reviewList);
    }

    public ReturnReviewDTO getReviewDataByPlace(int placeId) {
        List<Review> reviewList = reviewRepository.findByPlaceId(placeId);
        setReviewPhoto(reviewList);
        if (reviewList.size() <= 0)
            return null;

        float ratingAvg = 0;
        for (Review review : reviewList)
            ratingAvg += review.getRating();

        ratingAvg /= reviewList.size();

        ReturnReviewDTO returnReviewDTO = new ReturnReviewDTO(setReadReviewDTO(reviewList), ratingAvg);

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
