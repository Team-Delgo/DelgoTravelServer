package com.delgo.api.controller;

import com.delgo.api.comm.CommController;
import com.delgo.api.comm.oauth.KakaoService;
import com.delgo.api.comm.exception.ApiCode;
import com.delgo.api.comm.security.jwt.Access_JwtProperties;
import com.delgo.api.comm.security.jwt.Refresh_JwtProperties;
import com.delgo.api.domain.pet.Pet;
import com.delgo.api.domain.user.User;
import com.delgo.api.domain.user.UserSocial;
import com.delgo.api.dto.KakaoDTO;
import com.delgo.api.dto.user.UserPetDTO;
import com.delgo.api.service.PetService;
import com.delgo.api.service.TokenService;
import com.delgo.api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OAuthController extends CommController {
    private final UserService userService;
    private final PetService petService;
    private final KakaoService kakaoService;
    private final TokenService tokenService;

    // 회원가입 전 인증번호 전송
    @PostMapping(value = {"/setAccessCode/kakao/{accessCode}","/setAccessCode/kakao/"})
    public ResponseEntity<?> setAccessCode(@PathVariable String accessCode, HttpServletResponse response) throws Exception {
        log.info("accessCode : {}", accessCode);
        String accessToken = kakaoService.getKakaoAccessToken(accessCode);
        log.info("accessToken : {}", accessToken);
        KakaoDTO kakaoDTO = kakaoService.createKakaoUser(accessToken);
        String kakaoPhoneNo = kakaoDTO.getPhoneNo();

        // TODO : 카카오 에러 // 1000
        if(kakaoPhoneNo.equals(""))
            return ErrorReturn(ApiCode.UNKNOWN_ERROR);

        // TODO : 카카오 전화번호 X
        if(kakaoPhoneNo.equals("PhoneNoNotExist"))
            return ErrorReturn(ApiCode.KAKAO_PHONE_NO_NOT_EXIST);

        kakaoPhoneNo = kakaoPhoneNo.replaceAll("[^0-9]", "");
        kakaoPhoneNo = "010" + kakaoPhoneNo.substring(kakaoPhoneNo.length()-8, kakaoPhoneNo.length());
        kakaoDTO.setPhoneNo(kakaoPhoneNo);
        log.info("kakaoPhoneNo : {}", kakaoPhoneNo);

        // TODO : 카카오 전화번호 0 ,  DB 전화번호 X // PhoneNo 반환
        if(!userService.isPhoneNoExisting(kakaoPhoneNo))
            return ErrorReturn(ApiCode.PHONE_NO_NOT_EXIST, kakaoDTO);

        User user = userService.getUserByPhoneNo(kakaoPhoneNo);
        kakaoDTO.setUserSocial(user.getUserSocial());

        // TODO : 카카오 전화번호 0 , DB 전화번호 0, 카카오 연동 X // 현재 연동된 OAuth 코드 반환
        kakaoDTO.setEmail(user.getEmail());
        if(user.getUserSocial() != UserSocial.K)
            return ErrorReturn(ApiCode.KAKAO_NOT_CONNECT, kakaoDTO);

        Pet pet = petService.getPetByUserId(user.getUserId());
        String Access_jwtToken = tokenService.createToken("Access", user.getEmail()); // Access Token 생성
        String Refresh_jwtToken = tokenService.createToken("Refresh", user.getEmail()); // Refresh Token 생성

        response.addHeader(Access_JwtProperties.HEADER_STRING, Access_JwtProperties.TOKEN_PREFIX + Access_jwtToken);
        response.addHeader(Refresh_JwtProperties.HEADER_STRING, Refresh_JwtProperties.TOKEN_PREFIX + Refresh_jwtToken);

        return SuccessReturn(new UserPetDTO(user, pet)); //200
    }
}
