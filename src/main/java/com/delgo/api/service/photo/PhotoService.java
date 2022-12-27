package com.delgo.api.service.photo;

import com.delgo.api.comm.CommService;
import com.delgo.api.comm.ncp.service.ObjectStorageService;
import com.delgo.api.domain.photo.ReviewPhoto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PhotoService extends CommService {

    private final String DIR = "/var/www/delgo-api/";
    private final ObjectStorageService objectStorageService;

    public String uploadProfile(int userId, MultipartFile photo) {
        // ex) png, jpg, jpeg
        String[] type = Objects.requireNonNull(photo.getOriginalFilename()).split("\\."); // ex) png, jpg, jpeg
        String extension = type[type.length - 1];
        if (!extension.equals("png") && !extension.equals("jpg") && !extension.equals("jpeg") && !extension.equals("jfif"))
            throw new NullPointerException("PHOTO EXTENSION IS WRONG");

        String fileName = userId + "_profile." + extension;
        String ncpLink = "https://kr.object.ncloudstorage.com/delgo-profile/" + fileName; // NCP Link

        try {
            File f = new File(DIR + fileName); // 서버에 저장
            photo.transferTo(f);

            if (f.exists()) {
                objectStorageService.uploadObjects("reward-profile", fileName, DIR + fileName); // Upload NCP
                f.delete(); // 서버에 저장된 사진 삭제
            }

            ncpLink += "?" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddhhmmss")) + numberGen(4, 1); // Cache 무효화
            return ncpLink;
        } catch (Exception e) {
            return "error:" + e.getMessage();
        }
    }

    public ReviewPhoto uploadReviewPhoto(int reviewId, MultipartFile photo) {
        // ex) png, jpg, jpeg
        String[] type = Objects.requireNonNull(photo.getOriginalFilename()).split("\\.");
        String extension = type[type.length - 1];
        if (!extension.equals("png") && !extension.equals("jpg") && !extension.equals("jpeg") && !extension.equals("jfif"))
            throw new NullPointerException("PHOTO EXTENSION IS WRONG");

        String fileName = reviewId + "_review_"
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddhhmmss")) + numberGen(4, 1)
                + "." + extension;
        String link = "https://kr.object.ncloudstorage.com/delgo-review/" + fileName; //NCP Link

        try {
            File f = new File(DIR + fileName); // 서버에 저장
            photo.transferTo(f);

            if (f.exists()) {
                objectStorageService.uploadObjects("delgo-review", fileName, DIR + fileName); // Upload NCP
                f.delete(); // 서버에 저장된 사진 삭제
            }

            link += "?" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddhhmmss")) + numberGen(4, 1); // Cache 무효화
            return ReviewPhoto.builder()
                    .reviewId(reviewId)
                    .url(link)
                    .build();
        } catch (Exception e) {
            throw new NullPointerException("PHOTO UPLOAD NCP ERROR");
        }
    }
}
