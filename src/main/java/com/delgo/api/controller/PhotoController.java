package com.delgo.api.controller;

import com.delgo.api.comm.CommController;
import com.delgo.api.comm.exception.ApiCode;
import com.delgo.api.domain.Review;
import com.delgo.api.domain.user.User;
import com.delgo.api.service.PhotoService;
import com.delgo.api.service.ReviewService;
import com.delgo.api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/photo")
public class PhotoController extends CommController {

    private final UserService userService;
    private final PhotoService photoService;
    private final ReviewService reviewService;

    /*
     * PetProfile 등록 및 수정 [회원가입 or AccountPage]
     * Request Data : userId, photo
     * - userId : NCP에 올릴 Photo File명에 사용됨. & DB에 photoUrl 등록할 때 User 식별
     * - photo : 확장자는 .jpg를 기본으로 한다. [.jpeg도 가능] [ 따로 확장자를 체크하진 않는다.]
     * Response Data : ApiCode
     */
    @PostMapping("/upload/petProfile")
    public ResponseEntity<?> uploadPetProfile(@RequestParam Integer userId, @RequestPart MultipartFile photo) {
        if (photo.isEmpty()) // Validate - Empty Check;
            return ErrorReturn(ApiCode.PARAM_ERROR);

        String profileUrl = photoService.uploadPetProfile(userId, photo);
        if (profileUrl.split(":")[0].equals("error")) { //NCP ERROR
            log.info("NCP ERROR : {}", profileUrl.split(":")[1]);
            return ErrorReturn(ApiCode.PHOTO_UPLOAD_ERROR);
        }

        User user = userService.getUserByUserId(userId);
        user.setProfile(profileUrl);

        userService.updateUserData(user);

        return SuccessReturn(profileUrl);
    }

    /*
     * ReviewPhoto 등록 및 수정 [Review 작성 및 수정]
     * - 리뷰 사진은 최대 5개까지 저장 가능
     * Request Data : reviewId, photos
     * - reviewId : DB에 photoUrl 등록할 때 Review 식별
     * - photos: List이다, 0 ~ 4개 허용, 확장자는 .jpg를 기본으로 한다. [ 리뷰는 사진이 필수가 아니므로 *빈 값 허용한다.*  ]
     * Response Data : ApiCode
     */
    @PostMapping(value = {"/upload/reviewPhoto/{reviewId}", "/upload/reviewPhoto"})
    public ResponseEntity<?> uploadReviewPhoto(
            @PathVariable Integer reviewId,
            @RequestPart List<MultipartFile> photos) {
        log.info("uploadReviewPhoto reviewId: {}", reviewId);
        log.info("uploadReviewPhoto photos size: {} ", photos.size());

        if (!reviewService.isReviewExisting(reviewId))
            return ErrorReturn(ApiCode.REVIEW_NOT_EXIST);
        if (photos.isEmpty() || photos.size() > 4 )
            return ErrorReturn(ApiCode.REVIEW_PHOTO_COUNT_ERROR);

        Review review = reviewService.getReviewDataByReview(reviewId);

        int i = 1;
        for (MultipartFile photo : photos) {
            String reviewUrl = photoService.uploadReviewPhoto(reviewId, i++, photo);
            if (reviewUrl.split(":")[0].equals("error"))
                return ErrorReturn(ApiCode.PHOTO_UPLOAD_ERROR); //NCP ERROR
        }

        return SuccessReturn(review);
    }
}
