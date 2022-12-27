package com.delgo.api.service.photo;

import com.delgo.api.domain.photo.ReviewPhoto;
import com.delgo.api.repository.photo.ReviewPhotoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewPhotoService {

    private final ReviewPhotoRepository reviewPhotoRepository;

    public List<ReviewPhoto> getReviewPhotos(int reviewId) {
        return reviewPhotoRepository.findByReviewId(reviewId);
    }

    public void delete(int reviewId) {
        reviewPhotoRepository.deleteByReviewId(reviewId);
    }
}
