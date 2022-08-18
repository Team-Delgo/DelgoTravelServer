package com.delgo.api.controller;

import com.delgo.api.comm.CommController;
import com.delgo.api.comm.exception.ApiCode;
import com.delgo.api.comm.security.jwt.Access_JwtProperties;
import com.delgo.api.comm.security.jwt.Refresh_JwtProperties;
import com.delgo.api.domain.SmsAuth;
import com.delgo.api.domain.pet.Pet;
import com.delgo.api.domain.user.User;
import com.delgo.api.domain.user.UserSocial;
import com.delgo.api.dto.user.*;
import com.delgo.api.service.PetService;
import com.delgo.api.service.SmsAuthService;
import com.delgo.api.service.TokenService;
import com.delgo.api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController extends CommController {
    private final PasswordEncoder passwordEncoder;

    private final UserService userService;
    private final PetService petService;
    private final TokenService tokenService;
    private final SmsAuthService smsAuthService;

    @GetMapping("/myAccount")
    public ResponseEntity<?> myAccount(@RequestParam Integer userId) {
        InfoDTO infoDTO = userService.getInfoByUserId(userId);

        return SuccessReturn(infoDTO);
    }

    // 펫 정보 수정
    @PostMapping("/changePetInfo")
    public ResponseEntity<?> changePetInfo(@Validated @RequestBody ModifyPetDTO modifyPetDTO) {
        String checkedEmail = modifyPetDTO.getEmail();

        User user = userService.getUserByEmail(checkedEmail);
        int userId = user.getUserId();
        Pet originPet = petService.getPetByUserId(userId);

        if (modifyPetDTO.getName() != null)
            originPet.setName(modifyPetDTO.getName());

        if (modifyPetDTO.getSize() != null)
            originPet.setSize(modifyPetDTO.getSize());

        petService.changePetInfo(originPet);

        return SuccessReturn();
    }

    // 비밀번호 변경 - Account Page
    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(@Validated @RequestBody ResetPasswordDTO resetPassword) {
        // 사용자 확인 - 토큰 사용
        userService.changePassword(resetPassword.getEmail(), resetPassword.getNewPassword());
        return SuccessReturn();
    }

    // 비밀번호 재설정
    @PostMapping("/resetPassword")
    public ResponseEntity<?> resetPassword(@Validated @RequestBody ResetPasswordDTO resetPasswordDTO) {
        User user = userService.getUserByEmail(resetPasswordDTO.getEmail()); // 유저 조회
        SmsAuth smsAuth = smsAuthService.getSmsAuthByPhoneNo(user.getPhoneNo()); // SMS DATA 조회
        if (!smsAuthService.isAuth(smsAuth))
            ErrorReturn(ApiCode.SMS_ERROR);

        userService.changePassword(resetPasswordDTO.getEmail(), resetPasswordDTO.getNewPassword());
        return SuccessReturn();
    }

    // 이메일 존재 유무 확인
    @GetMapping("/emailAuth")
    public ResponseEntity<?> emailAuth(@RequestParam String email) {
        if (email.isBlank()) {
            return ErrorReturn(ApiCode.PARAM_ERROR);
        }

        if (userService.isEmailExisting(email)) {
            return SuccessReturn(userService.getUserByEmail(email).getPhoneNo());
        }
        return ErrorReturn(ApiCode.EMAIL_NOT_EXIST);
    }

    // 이메일 중복 확인
    @GetMapping("/emailCheck")
    public ResponseEntity<?> emailCheck(@RequestParam String email) {
        if (email.isBlank()) {
            return ErrorReturn(ApiCode.PARAM_ERROR);
        }
        if (!userService.isEmailExisting(email)) {
            return SuccessReturn();
        } else {
            return ErrorReturn(ApiCode.EMAIL_DUPLICATE_ERROR);
        }
    }

    // 이름 중복 확인
    @GetMapping("/nameCheck")
    public ResponseEntity<?> nameCheck(@RequestParam String name) {
        if (name.isBlank()) {
            return ErrorReturn(ApiCode.PARAM_ERROR);
        }
        if (!userService.isNameExisting(name))
            return SuccessReturn();
        else
            return ErrorReturn(ApiCode.NAME_DUPLICATE_ERROR);
    }

    // 소셜 회원가입
    @PostMapping("/oAuthSignup")
    public ResponseEntity<?> registerUserByOAuth(@Validated @RequestBody OAuthSignUpDTO signUpDTO, HttpServletResponse response) {
        User user = User.builder()
                .name(signUpDTO.getUserName())
                .phoneNo(signUpDTO.getPhoneNo().replaceAll("[^0-9]", ""))
                .email("")
                .password("")
                .userSocial(signUpDTO.getUserSocial())
                .build();
        Pet pet = Pet.builder()
                .name(signUpDTO.getPetName())
                .size(signUpDTO.getPetSize())
                .birthday(signUpDTO.getBirthday())
                .build();

        User userByDB = userService.signup(user, pet);
        Pet petByDB = petService.getPetByUserId(user.getUserId());

        String Access_jwtToken = tokenService.createToken("Access", user.getEmail()); // Access Token 생성
        String Refresh_jwtToken = tokenService.createToken("Refresh", user.getEmail()); // Refresh Token 생성

        response.addHeader(Access_JwtProperties.HEADER_STRING, Access_JwtProperties.TOKEN_PREFIX + Access_jwtToken);
        response.addHeader(Refresh_JwtProperties.HEADER_STRING, Refresh_JwtProperties.TOKEN_PREFIX + Refresh_jwtToken);

        return SuccessReturn(new UserPetDTO(userByDB, petByDB));
    }

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Validated @RequestBody SignUpDTO signUpDTO, HttpServletResponse response) {
        if (userService.isEmailExisting(signUpDTO.getEmail())) // Email 중복확인
            return ErrorReturn(ApiCode.EMAIL_DUPLICATE_ERROR);

        User user = User.builder()
                .name(signUpDTO.getUserName())
                .email(signUpDTO.getEmail())
                .password(passwordEncoder.encode(signUpDTO.getPassword()))
                .phoneNo(signUpDTO.getPhoneNo().replaceAll("[^0-9]", ""))
                .userSocial(UserSocial.D)
                .build();
        Pet pet = Pet.builder()
                .name(signUpDTO.getPetName())
                .size(signUpDTO.getPetSize())
                .birthday(signUpDTO.getBirthday())
                .build();

        User userByDB = userService.signup(user, pet);
//        user.setPassword(""); // 보안
        Pet petByDB = petService.getPetByUserId(user.getUserId());

        String Access_jwtToken = tokenService.createToken("Access", user.getEmail()); // Access Token 생성
        String Refresh_jwtToken = tokenService.createToken("Refresh", user.getEmail()); // Refresh Token 생성

        response.addHeader(Access_JwtProperties.HEADER_STRING, Access_JwtProperties.TOKEN_PREFIX + Access_jwtToken);
        response.addHeader(Refresh_JwtProperties.HEADER_STRING, Refresh_JwtProperties.TOKEN_PREFIX + Refresh_jwtToken);

        return SuccessReturn(new UserPetDTO(userByDB, petByDB));
    }

    // 회원탈퇴
    @PostMapping(value = {"/deleteUser/{userId}", "/deleteUser"})
    public ResponseEntity<?> deleteUser(@PathVariable(value = "userId") Integer userId) {

        userService.deleteUser(userId);
        return SuccessReturn();
    }
}
