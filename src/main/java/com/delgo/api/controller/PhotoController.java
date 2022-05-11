package com.delgo.api.controller;

import com.delgo.api.domain.user.User;
import com.delgo.api.dto.common.ResponseDTO;
import com.delgo.api.service.PhotoService;
import com.delgo.api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/photo")
public class PhotoController {

    private final PhotoService photoService;
    private final UserService userService;

    @PostMapping("/upload/petProfile")
    public ResponseEntity<?> uploadPhoto(
            @RequestParam(value = "userId", required = true) int userId,
            @RequestParam(value = "file", required = true) MultipartFile file) {
        try {
            //TODO: 만약 파일이 존재해도 덮어써지는걸로 보임. ( 나중에 확인할 것 )
            String profileUrl = photoService.uploadPetProfile(userId, file);
            User user = userService.findByUserId(userId);
            user.setProfile(profileUrl);

            userService.updateUserData(user);

            return ResponseEntity.ok().body(ResponseDTO.builder().code(200).codeMsg("PetProfile Upload Success").build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ResponseDTO.builder().code(303).codeMsg(e.getMessage()).build());
        }
    }
}
