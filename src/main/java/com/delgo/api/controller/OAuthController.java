package com.delgo.api.controller;

import com.delgo.api.comm.CommController;
import com.delgo.api.comm.oauth.AppleService;
import com.delgo.api.comm.oauth.KakaoService;
import com.delgo.api.comm.exception.ApiCode;
import com.delgo.api.comm.oauth.NaverService;
import com.delgo.api.comm.security.jwt.JwtService;
import com.delgo.api.comm.security.jwt.JwtToken;
import com.delgo.api.comm.security.jwt.config.AccessTokenProperties;
import com.delgo.api.comm.security.jwt.config.RefreshTokenProperties;
import com.delgo.api.domain.pet.Pet;
import com.delgo.api.domain.user.User;
import com.delgo.api.domain.user.UserSocial;
import com.delgo.api.dto.OAuthDTO;
import com.delgo.api.dto.user.UserResDTO;
import com.delgo.api.service.PetService;
import com.delgo.api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("oauth")
public class OAuthController extends CommController {

    private final JwtService jwtService;
    private final PetService petService;
    private final UserService userService;
    private final KakaoService kakaoService;
    private final NaverService naverService;
    private final AppleService appleService;

    // Apple
    @PostMapping(value = {"/apple/{id_token}", "/apple"})
    public ResponseEntity oauthApple(@PathVariable String id_token, HttpServletResponse response) {
        JSONObject payload = appleService.decodeFromIdToken(id_token);
        String appleUniqueNo = payload.getAsString("sub");  //  회원 고유 식별자
        log.info("appleUniqueNo : {}", appleUniqueNo);

        // DB에 appleUniqueNo 존재 X
        if (!userService.isAppleUniqueNoExisting(appleUniqueNo))
            return ErrorReturn(ApiCode.APPLE_UNIQUE_NO_NOT_FOUND, appleUniqueNo);
        
        // DB에 appleUniqueNo 존재 O -> 해당 User 반환
        User user = userService.getUserByAppleUniqueNo(appleUniqueNo);
        Pet pet = petService.getPetByUserId(user.getUserId());

        JwtToken jwt = jwtService.createToken(user.getUserId());
        response.addHeader(AccessTokenProperties.HEADER_STRING, AccessTokenProperties.TOKEN_PREFIX + jwt.getAccessToken());
        response.addHeader(RefreshTokenProperties.HEADER_STRING, RefreshTokenProperties.TOKEN_PREFIX + jwt.getRefreshToken());

        return SuccessReturn(new UserResDTO(user, pet));
    }

    // Kakao
    @PostMapping(value = {"/kakao/{code}","/kakao"})
    public ResponseEntity<?> oauthKakao(@PathVariable String code, HttpServletResponse response) throws Exception {
        String accessToken = kakaoService.getKakaoAccessToken(code);
        OAuthDTO oAuthDTO = kakaoService.createKakaoUser(accessToken);
        String kakaoPhoneNo = oAuthDTO.getPhoneNo();

        // 카카오 에러 : 1000
        if(kakaoPhoneNo.equals(""))
            return ErrorReturn(ApiCode.UNKNOWN_ERROR);

        // 카카오 전화번호 X
        if(kakaoPhoneNo.equals("PhoneNoNotExist"))
            return ErrorReturn(ApiCode.OAUTH_PHONE_NO_NOT_EXIST);

        kakaoPhoneNo = kakaoPhoneNo.replaceAll("[^0-9]", "");
        kakaoPhoneNo = "010" + kakaoPhoneNo.substring(kakaoPhoneNo.length()-8);
        oAuthDTO.setPhoneNo(kakaoPhoneNo);

        // 카카오 전화번호 0 , DB 전화번호 X -> PhoneNo 반환
        if(!userService.isPhoneNoExisting(kakaoPhoneNo))
            return ErrorReturn(ApiCode.PHONE_NO_NOT_EXIST, oAuthDTO);

        User user = userService.getUserByPhoneNo(kakaoPhoneNo);
        oAuthDTO.setUserSocial(user.getUserSocial());

        // 카카오 전화번호 0 , DB 전화번호 0, 카카오 연동 X -> 현재 연동된 OAuth 코드 반환
        oAuthDTO.setEmail(user.getEmail());
        if(user.getUserSocial() != UserSocial.K)
            return ErrorReturn(ApiCode.ANOTHER_OAUTH_CONNECT, oAuthDTO);

        Pet pet = petService.getPetByUserId(user.getUserId());

        JwtToken jwt = jwtService.createToken(user.getUserId());
        response.addHeader(AccessTokenProperties.HEADER_STRING, AccessTokenProperties.TOKEN_PREFIX + jwt.getAccessToken());
        response.addHeader(RefreshTokenProperties.HEADER_STRING, RefreshTokenProperties.TOKEN_PREFIX + jwt.getRefreshToken());

        return SuccessReturn(new UserResDTO(user, pet)); //200
    }

    // Naver
    @PostMapping(value = {"/naver/{state}/{code}","/naver"})
    public ResponseEntity<?> oauthNaver(@PathVariable String state, @PathVariable String code, HttpServletResponse response) throws Exception {
        String accessToken = naverService.getNaverAccessToken(state, code);
        OAuthDTO oAuthDTO = naverService.createNaverUser(accessToken);
        String naverPhoneNo = oAuthDTO.getPhoneNo();

        // 네이버 에러 : 1000
        if(naverPhoneNo.equals(""))
            return ErrorReturn(ApiCode.UNKNOWN_ERROR);

        // 네이버 전화번호 X
        if(naverPhoneNo.equals("PhoneNoNotExist"))
            return ErrorReturn(ApiCode.OAUTH_PHONE_NO_NOT_EXIST);

        naverPhoneNo = naverPhoneNo.replaceAll("[^0-9]", "");
        oAuthDTO.setPhoneNo(naverPhoneNo);

        // 네이버 전화번호 0 , DB 전화번호 X -> PhoneNo 반환
        if(!userService.isPhoneNoExisting(naverPhoneNo))
            return ErrorReturn(ApiCode.PHONE_NO_NOT_EXIST, oAuthDTO);

        User user = userService.getUserByPhoneNo(naverPhoneNo);
        oAuthDTO.setUserSocial(user.getUserSocial());

        // 네이버 전화번호 0 , DB 전화번호 0, 네이버 연동 X -> 현재 연동된 OAuth 코드 반환
        oAuthDTO.setEmail(user.getEmail());
        if(user.getUserSocial() != UserSocial.N)
            return ErrorReturn(ApiCode.ANOTHER_OAUTH_CONNECT, oAuthDTO);

        Pet pet = petService.getPetByUserId(user.getUserId());

        JwtToken jwt = jwtService.createToken(user.getUserId());
        response.addHeader(AccessTokenProperties.HEADER_STRING, AccessTokenProperties.TOKEN_PREFIX + jwt.getAccessToken());
        response.addHeader(RefreshTokenProperties.HEADER_STRING, RefreshTokenProperties.TOKEN_PREFIX + jwt.getRefreshToken());

        return SuccessReturn(new UserResDTO(user, pet)); //200
    }
}
