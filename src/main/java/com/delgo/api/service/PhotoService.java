package com.delgo.api.service;

import com.delgo.api.config.ncp.objectStorage.ObjectStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Objects;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PhotoService {

    private final ObjectStorageService objectStorageService;

    // NCP에 petProfile Upload 후 접근 URL 반환
    public String uploadPetProfile(int userId, MultipartFile file) {
        // ex) png, jpg, jpeg
        String[] type = Objects.requireNonNull(file.getOriginalFilename()).split("\\.");

        String fileName = userId + "_pet_profile." + type[type.length - 1];
        String dir = "/var/www/delgo-api/";
        String link = "https://kr.object.ncloudstorage.com/delgo-pet-profile/" + fileName;

        try {
            // 서버에 저장
            File f = new File(dir + fileName);
            file.transferTo(f);

            // Upload NCP
            objectStorageService.uploadObjects("delgo-pet-profile", fileName, dir + fileName);

            // 서버에 저장된 사진 삭제
            if (f.exists()) f.delete();

            // NCP Link
            return link;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "error";
    }

    // NCP에 ReviewPhoto Upload 후 접근 URL 반환
    public String uploadReviewPhoto(int reviewId, int order, MultipartFile file) {
        // ex) png, jpg, jpeg
        String[] type = Objects.requireNonNull(file.getOriginalFilename()).split("\\.");

        String fileName = reviewId + "_review" + order + "." + type[type.length - 1];
        String dir = "/var/www/delgo-api/";
        String link = "https://kr.object.ncloudstorage.com/delgo-review/" + fileName;

        try {
            // 서버에 저장
            File f = new File(dir + fileName);
            file.transferTo(f);

            // Upload NCP
            objectStorageService.uploadObjects("delgo-review", fileName, dir + fileName);

            // 서버에 저장된 사진 삭제
            if (f.exists()) f.delete();

            // NCP Link
            return link;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "error";
    }
}
