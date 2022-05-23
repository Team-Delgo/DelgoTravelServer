package com.delgo.api.controller;

import com.delgo.api.domain.Review;
import com.delgo.api.domain.user.User;
import com.delgo.api.dto.common.ResponseDTO;
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
public class PhotoController {

    private final PhotoService photoService;
    private final UserService userService;
    private final ReviewService reviewService;

    @PostMapping("/upload/petProfile")
    public ResponseEntity<?> uploadPetProfile(
            @RequestParam(value = "userId", required = true) int userId,
            @RequestParam(value = "file", required = true) MultipartFile file) {
        try {
            String profileUrl = photoService.uploadPetProfile(userId, file);
            if (profileUrl.split(":")[0].equals("error")) //NCP ERROR
                return ResponseEntity.ok().body(
                        ResponseDTO.builder().code(303).codeMsg(profileUrl.split(":")[1]).build());

            User user = userService.findByUserId(userId);
            user.setProfile(profileUrl);

            userService.updateUserData(user);

            return ResponseEntity.ok().body(
                    ResponseDTO.builder().code(200).data(profileUrl).codeMsg("PetProfile Upload Success").build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ResponseDTO.builder().code(303).codeMsg(e.getMessage()).build());
        }
    }

    @PostMapping("/upload/reviewPhoto")
    public ResponseEntity<?> uploadReviewPhoto(
            @RequestParam int reviewId,
            @RequestParam(value = "photo1", required = false) Optional<MultipartFile> photo1,
            @RequestParam(value = "photo2", required = false) Optional<MultipartFile> photo2,
            @RequestParam(value = "photo3", required = false) Optional<MultipartFile> photo3,
            @RequestParam(value = "photo4", required = false) Optional<MultipartFile> photo4,
            @RequestParam(value = "photo5", required = false) Optional<MultipartFile> photo5) {
        // Review 존재 여부 확인
        Optional<Review> review = reviewService.getReviewDataByReview(reviewId);
        if (!review.isPresent())
            return ResponseEntity.ok().body(
                    ResponseDTO.builder().code(303).codeMsg("ReviewId is Wrong.").build());
        Review checkdReview = review.get();

        // data 초기화
        checkdReview.setReviewPhoto1(null);
        checkdReview.setReviewPhoto2(null);
        checkdReview.setReviewPhoto3(null);
        checkdReview.setReviewPhoto4(null);
        checkdReview.setReviewPhoto5(null);

        List<MultipartFile> multiList = new ArrayList<MultipartFile>();
        if (photo1.isPresent()) multiList.add(photo1.get());
        if (photo2.isPresent()) multiList.add(photo2.get());
        if (photo3.isPresent()) multiList.add(photo3.get());
        if (photo4.isPresent()) multiList.add(photo4.get());
        if (photo5.isPresent()) multiList.add(photo5.get());


        for (int i = 0; i < multiList.size(); i++) {
            String reviewUrl = photoService.uploadReviewPhoto(reviewId, i + 1, multiList.get(i));
            if (reviewUrl.split(":")[0].equals("error")) //NCP ERROR
                return ResponseEntity.ok().body(
                        ResponseDTO.builder().code(303).codeMsg(reviewUrl.split(":")[1]).build());
            switch (i) {
                case 0: checkdReview.setReviewPhoto1(reviewUrl); break;
                case 1: checkdReview.setReviewPhoto2(reviewUrl); break;
                case 2: checkdReview.setReviewPhoto3(reviewUrl); break;
                case 3: checkdReview.setReviewPhoto4(reviewUrl); break;
                case 4: checkdReview.setReviewPhoto5(reviewUrl); break;
            }
        }
        Review updatedReview = reviewService.insertOrUpdateReview(checkdReview);

        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(200).data(updatedReview).codeMsg("ReviewPhoto Upload Success").build());
    }
}
