package com.delgo.api.controller;

import com.delgo.api.comm.CommController;
import com.delgo.api.comm.exception.ApiCode;
import com.delgo.api.comm.security.jwt.JwtService;
import com.delgo.api.comm.security.jwt.JwtToken;
import com.delgo.api.comm.security.jwt.config.AccessTokenProperties;
import com.delgo.api.comm.security.jwt.config.RefreshTokenProperties;
import com.delgo.api.domain.SmsAuth;
import com.delgo.api.domain.pet.Pet;
import com.delgo.api.domain.user.User;
import com.delgo.api.domain.user.UserSocial;
import com.delgo.api.dto.user.OAuthSignUpDTO;
import com.delgo.api.dto.user.SignUpDTO;
import com.delgo.api.dto.user.*;
import com.delgo.api.service.PetService;
import com.delgo.api.service.SmsAuthService;
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
@RequestMapping("/user")
public class UserController extends CommController {
    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;
    private final PetService petService;
    private final UserService userService;
    private final SmsAuthService smsAuthService;

    // 비밀번호 재설정
    @PutMapping("/password")
    public ResponseEntity<?> resetPassword(@Validated @RequestBody ResetPasswordDTO resetPasswordDTO) {
        User user = userService.getUserByEmail(resetPasswordDTO.getEmail()); // 유저 조회
        SmsAuth smsAuth = smsAuthService.getSmsAuthByPhoneNo(user.getPhoneNo()); // SMS DATA 조회
        if (!smsAuthService.isAuth(smsAuth))
            ErrorReturn(ApiCode.SMS_ERROR);

        userService.changePassword(resetPasswordDTO.getEmail(), resetPasswordDTO.getNewPassword());
        return SuccessReturn();
    }


    // 소셜 회원가입
    @PostMapping("/oauth")
    public ResponseEntity<?> registerUserByOAuth(@Validated @RequestBody OAuthSignUpDTO oAuthSignUpDTO, HttpServletResponse response) {
        User user;

        // Apple 회원가입 시 appleUniqueNo 넣어주어야 함.
        if (oAuthSignUpDTO.getUserSocial() == UserSocial.A) {
            if (oAuthSignUpDTO.getAppleUniqueNo() == null || oAuthSignUpDTO.getAppleUniqueNo().isBlank())
                return ErrorReturn(ApiCode.PARAM_ERROR);
            user = userService.signup(
                    oAuthSignUpDTO.makeUserApple(oAuthSignUpDTO.getAppleUniqueNo()),
                    oAuthSignUpDTO.makePet()
            );
//                user.setAppleUniqueNo(signUpDTO.getAppleUniqueNo());
        }
        else {
            user = userService.signup(
                    oAuthSignUpDTO.makeUserSocial(oAuthSignUpDTO.getUserSocial()),
                    oAuthSignUpDTO.makePet()
            );
        }

//        User userByDB = userService.signup(user, pet);
//        Pet petByDB = petService.getPetByUserId(user.getUserId());

        Pet pet = petService.getPetByUserId(user.getUserId());

        JwtToken jwt = jwtService.createToken(user.getUserId());
        response.addHeader(AccessTokenProperties.HEADER_STRING, AccessTokenProperties.TOKEN_PREFIX + jwt.getAccessToken());
        response.addHeader(RefreshTokenProperties.HEADER_STRING, RefreshTokenProperties.TOKEN_PREFIX + jwt.getRefreshToken());

        return SuccessReturn(new UserResDTO(user, pet));
    }

    // 회원가입
    @PostMapping
    public ResponseEntity<?> registerUser(@Validated @RequestBody SignUpDTO signUpDTO, HttpServletResponse response) {
        if (userService.isEmailExisting(signUpDTO.getEmail())) // Email 중복확인
            return ErrorReturn(ApiCode.EMAIL_DUPLICATE_ERROR);

        User user = userService.signup(
                signUpDTO.makeUser(passwordEncoder.encode(signUpDTO.getPassword())), // USER
                signUpDTO.makePet() // PET
        );

        JwtToken jwt = jwtService.createToken(user.getUserId());
        response.addHeader(AccessTokenProperties.HEADER_STRING, AccessTokenProperties.TOKEN_PREFIX + jwt.getAccessToken());
        response.addHeader(RefreshTokenProperties.HEADER_STRING, RefreshTokenProperties.TOKEN_PREFIX + jwt.getRefreshToken());

        return SuccessReturn(new UserResDTO(user, petService.getPetByUserId(user.getUserId())));
    }
}
