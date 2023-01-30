package com.delgo.api.controller;

import com.delgo.api.comm.CommController;
import com.delgo.api.comm.exception.ApiCode;
import com.delgo.api.domain.photo.ReviewPhoto;
import com.delgo.api.service.photo.PhotoService;
import com.delgo.api.service.ReviewService;
import com.delgo.api.service.UserService;
import com.delgo.api.service.photo.ReviewPhotoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/photo")
public class PhotoController extends CommController {

    private final UserService userService;
    private final PhotoService photoService;
    private final ReviewService reviewService;
    private final ReviewPhotoService reviewPhotoService;

    /*
     * PetProfile 등록 및 수정 [회원가입 or AccountPage]
     * Request Data : userId, photo
     * - userId : NCP에 올릴 Photo File명에 사용됨. & DB에 photoUrl 등록할 때 User 식별
     * - photo : 확장자는 .jpg를 기본으로 한다. [.jpeg도 가능] [ 따로 확장자를 체크하진 않는다.]
     * Response Data : ApiCode
     */
    @PostMapping(value={"/upload/profile/{userId}","/upload/profile"})
    public ResponseEntity<?> uploadPetProfile(@PathVariable Integer userId, @RequestPart(required = false) MultipartFile photo) {
        if (photo.isEmpty()) return ErrorReturn(ApiCode.PARAM_ERROR);

        String ncpLink = photoService.uploadProfile(userId, photo);
        userService.register(userService.getUserById(userId).setProfile(ncpLink));

        return SuccessReturn(ncpLink);
    }

    /*
     * ReviewPhoto 등록 및 수정 [Review 작성 및 수정]
     * - 리뷰 사진은 최대 5개까지 저장 가능
     * Request Data : reviewId, photos
     * - reviewId : DB에 photoUrl 등록할 때 Review 식별
     * - photos: List이다,4개 까지 허용한다. (null 가능 == 0개 가능), 확장자는 .jpg를 기본으로 한다. [ 리뷰는 사진이 필수가 아니므로 *빈 값 허용한다.*  ]
     * Response Data : ApiCode
     */
    @PostMapping(value = {"/upload/reviewPhoto/{reviewId}", "/upload/reviewPhoto"})
    public ResponseEntity<?> uploadReviewPhoto(@PathVariable Integer reviewId, @RequestPart(required = false) List<MultipartFile> photos) {
        if (!reviewService.isReviewExisting(reviewId)) return ErrorReturn(ApiCode.REVIEW_NOT_EXIST);
        if (photos.isEmpty() || photos.size() > 4 ) return ErrorReturn(ApiCode.REVIEW_PHOTO_COUNT_ERROR);

        List<ReviewPhoto> savedReviewPhotos = reviewPhotoService.registerReviewPhotos(photos.stream()
                .map(photo -> photoService.uploadReviewPhoto(reviewId, photo))
                .collect(Collectors.toList()));

        return SuccessReturn(reviewService.getReviewById(reviewId).setReviewPhotos(savedReviewPhotos));
    }
}