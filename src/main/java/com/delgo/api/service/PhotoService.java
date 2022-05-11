package com.delgo.api.service;

import com.delgo.api.ncp.objectStorage.ObjectStorageService;
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
        String[] type = Objects.requireNonNull(file.getOriginalFilename()).split("\\.");

        String fileName = userId + "_pet_profile." + type[type.length - 1];
        String dir = "/var/www/delgo/";
        String link = "https://kr.object.ncloudstorage.com/delgo-pet-profile/" + fileName;

        try {
            // 서버에 저장
            File f = new File(dir + fileName);
            file.transferTo(f); // multipart를 이용한 저장

            // 서버에 저장된 파일 읽어서 NCP에 업로드
            objectStorageService.uploadObjects(fileName, dir + fileName);

            // 서버에 저장된 사진 삭제
            if (f.exists()) {
                f.delete();
            }

            // TODO: 저장된 NCP의 링크 반환
            return link;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "error";
    }
}
