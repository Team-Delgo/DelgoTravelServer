package com.delgo.api.service;

import com.delgo.api.comm.CommService;
import com.delgo.api.comm.ncp.service.ObjectStorageService;
import com.delgo.api.domain.photo.DetailPhoto;
import com.delgo.api.domain.photo.DetailRoomPhoto;
import com.delgo.api.domain.photo.ReviewPhoto;
import com.delgo.api.repository.DetailPhotoRepository;
import com.delgo.api.repository.DetailRoomPhotoRepository;
import com.delgo.api.repository.ReviewPhotoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PhotoService extends CommService {

    private final ObjectStorageService objectStorageService;
    private final DetailPhotoRepository detailPhotoRepository;
    private final DetailRoomPhotoRepository detailRoomPhotoRepository;
    private final ReviewPhotoRepository reviewPhotoRepository;

    // NCP에 petProfile Upload 후 접근 URL 반환
    public String uploadPetProfile(int userId, MultipartFile photo) {
        // ex) png, jpg, jpeg
        String[] type = Objects.requireNonNull(photo.getOriginalFilename()).split("\\.");

        String fileName = userId + "_pet_profile." + type[type.length - 1];
        String dir = "/var/www/delgo-api/";
        // NCP Link
        String link = "https://kr.object.ncloudstorage.com/delgo-pet-profile/" + fileName;

        try {
            // 서버에 저장
            File f = new File(dir + fileName);
            photo.transferTo(f);

            if (f.exists()) {
                // Upload NCP
                objectStorageService.uploadObjects("delgo-pet-profile", fileName, dir + fileName);

                // 서버에 저장된 사진 삭제
                f.delete();
            }

            // Cache 무효화
            link += "?" +LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddhhmmss")) + numberGen(4, 1);
            return link;
        } catch (Exception e) {
            return "error:" + e.getMessage();
        }
    }

    // NCP에 ReviewPhoto Upload 후 접근 URL 반환
    public String uploadReviewPhoto(int reviewId, int order, MultipartFile photo) {
        // ex) png, jpg, jpeg
        String[] type = Objects.requireNonNull(photo.getOriginalFilename()).split("\\.");

        String fileName = reviewId + "_review" + order + "." + type[type.length - 1];
        String dir = "/var/www/delgo-api/";
        String link = "https://kr.object.ncloudstorage.com/delgo-review/" + fileName; //NCP Link

        try {
            // 서버에 저장
            File f = new File(dir + fileName);
            photo.transferTo(f);

            if (f.exists()) {
                // Upload NCP
                objectStorageService.uploadObjects("delgo-review", fileName, dir + fileName);
                // 서버에 저장된 사진 삭제
                f.delete();
            }

            reviewPhotoRepository.save(ReviewPhoto.builder().reviewId(reviewId).url(link).build());
            // Cache 무효화
            link += "?" +LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddhhmmss")) + numberGen(4, 1);
            return link;
        } catch (Exception e) {
            return "error:" + e.getMessage();
        }
    }

    public List<DetailPhoto> getDetailPhotoList(int placeId) {
        return detailPhotoRepository.findByPlaceId(placeId);
    }

    public List<DetailRoomPhoto> getDetailRoomPhotoList(int roomId) {
        return detailRoomPhotoRepository.findByRoomId(roomId);
    }
}
