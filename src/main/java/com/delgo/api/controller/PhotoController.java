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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<?> uploadPetProfile(
            @RequestParam Integer userId,
            @RequestParam MultipartFile photo) {
        // Validate - Empty Check;
        if (photo.isEmpty())
            return ErrorReturn(ApiCode.PARAM_ERROR);

        String profileUrl = photoService.uploadPetProfile(userId, photo);
        //NCP ERROR
        if (profileUrl.split(":")[0].equals("error")) {
            log.info("NCP ERROR : {}", profileUrl.split(":")[1]);
            return ErrorReturn(ApiCode.PHOTO_UPLOAD_ERROR);
        }

        User user = userService.findByUserId(userId);
        user.setProfile(profileUrl);

        userService.updateUserData(user);

        return SuccessReturn();
    }

    /*
     * ReviewPhoto 등록 및 수정 [Review 작성 및 수정]
     * - 리뷰 사진은 최대 5개까지 저장 가능
     * Request Data : reviewId, photo1, photo2, photo3, photo4, photo5
     * - reviewId : DB에 photoUrl 등록할 때 Review 식별
     * - photo 1 ~ 5: 확장자는 .jpg를 기본으로 한다. [ 리뷰는 사진이 필수가 아니므로 *빈 값 허용한다.*  ]
     * Response Data : ApiCode
     */
    @PostMapping("/upload/reviewPhoto")
    public ResponseEntity<?> uploadReviewPhoto(
            @RequestParam Integer reviewId,
            @RequestParam(value = "photo1", required = false) MultipartFile photo1,
            @RequestParam(value = "photo2", required = false) MultipartFile photo2,
            @RequestParam(value = "photo3", required = false) MultipartFile photo3,
            @RequestParam(value = "photo4", required = false) MultipartFile photo4,
            @RequestParam(value = "photo5", required = false) MultipartFile photo5) {
        // Review 존재 여부 확인
        Optional<Review> review = reviewService.getReviewDataByReview(reviewId);
        if (review.isEmpty()) return ErrorReturn(ApiCode.REVIEW_NOT_EXIST);

        Review checkdReview = review.get();
        // data 초기화
        checkdReview.setReviewPhoto1(null);
        checkdReview.setReviewPhoto2(null);
        checkdReview.setReviewPhoto3(null);
        checkdReview.setReviewPhoto4(null);
        checkdReview.setReviewPhoto5(null);

        List<MultipartFile> multiList = new ArrayList<MultipartFile>();
        if (!photo1.isEmpty()) multiList.add(photo1);
        if (!photo2.isEmpty()) multiList.add(photo2);
        if (!photo3.isEmpty()) multiList.add(photo3);
        if (!photo4.isEmpty()) multiList.add(photo4);
        if (!photo5.isEmpty()) multiList.add(photo5);


        for (int i = 0; i < multiList.size(); i++) {
            String reviewUrl = photoService.uploadReviewPhoto(reviewId, i + 1, multiList.get(i));
            if (reviewUrl.split(":")[0].equals("error")) return ErrorReturn(ApiCode.PHOTO_UPLOAD_ERROR); //NCP ERROR
            switch (i) {
                case 0: checkdReview.setReviewPhoto1(reviewUrl); break;
                case 1: checkdReview.setReviewPhoto2(reviewUrl); break;
                case 2: checkdReview.setReviewPhoto3(reviewUrl); break;
                case 3: checkdReview.setReviewPhoto4(reviewUrl); break;
                case 4: checkdReview.setReviewPhoto5(reviewUrl); break;
            }
        }
        reviewService.insertOrUpdateReview(checkdReview);

        return SuccessReturn();
    }
}
