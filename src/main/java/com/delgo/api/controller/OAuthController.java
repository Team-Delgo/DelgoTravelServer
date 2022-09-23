package com.delgo.api.controller;

import com.delgo.api.comm.CommController;
import com.delgo.api.comm.oauth.AppleService;
import com.delgo.api.comm.oauth.KakaoService;
import com.delgo.api.comm.exception.ApiCode;
import com.delgo.api.comm.security.jwt.Access_JwtProperties;
import com.delgo.api.comm.security.jwt.Refresh_JwtProperties;
import com.delgo.api.domain.pet.Pet;
import com.delgo.api.domain.user.User;
import com.delgo.api.domain.user.UserSocial;
import com.delgo.api.dto.OAuthDTO;
import com.delgo.api.dto.user.UserPetDTO;
import com.delgo.api.service.PetService;
import com.delgo.api.service.TokenService;
import com.delgo.api.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OAuthController extends CommController {
    private final UserService userService;
    private final PetService petService;
    private final KakaoService kakaoService;
    private final AppleService appleService;
    private final TokenService tokenService;

    // 애플로그인
    public static final String TEAM_ID = "GGS2HY6B73";
    public static final String REDIRECT_URL = "https://delgo.pet/oauth/redirect/apple";
    public static final String CLIENT_ID = "pet.delgo";
    public static final String KEY_ID = "P9AZCBNYJG";
    public static final String AUTH_URL = "https://appleid.apple.com";
    public static final String KEY_PATH = "static/apple/AuthKey_P9AZCBNYJG.p8";

    @PostMapping("/getAppleAuthUrl")
    public ResponseEntity<?> getAppleAuthUrl(HttpServletRequest request) throws Exception {
        String reqUrl = AUTH_URL + "/auth/authorize?client_id=" + CLIENT_ID + "&redirect_url=" + REDIRECT_URL + "&response_type=code id_token&response_mode=form_post";

        return SuccessReturn(reqUrl);
    }

    @RequestMapping(value = "/oauth_apple")
    public ResponseEntity oauth_apple(HttpServletRequest request, @RequestParam(value = "code") String code, HttpServletResponse response) throws Exception {

        String client_secret = appleService.createClientSecret(TEAM_ID, CLIENT_ID, KEY_ID, KEY_PATH, AUTH_URL);

        String reqUrl = AUTH_URL + "/auth/token";

        Map<String, String> tokenRequest = new HashMap<>();

        tokenRequest.put("client_id", CLIENT_ID);
        tokenRequest.put("client_secret", client_secret);
        tokenRequest.put("code", code);
        tokenRequest.put("grant_type", "authorization_code");

        String apiResponse = appleService.doPost(reqUrl, tokenRequest);

        ObjectMapper objectMapper = new ObjectMapper();
        JSONObject tokenResponse = objectMapper.readValue(apiResponse, JSONObject.class);

        // 애플 정보조회 성공
        if (tokenResponse.get("error") == null ) {

            JSONObject payload = appleService.decodeFromIdToken(tokenResponse.getAsString("id_token"));
            //  회원 고유 식별자
            String appleUniqueNo = payload.getAsString("sub");

            /**

             TO DO : 리턴받은 appleUniqueNo 해당하는 회원정보 조회 후 로그인 처리 후 메인으로 이동

             */


            // 애플 정보조회 실패
        } else {
            return ErrorReturn(ApiCode.UNKNOWN_ERROR);
        }
        return SuccessReturn();
    }

    // 회원가입 전 인증번호 전송
    @PostMapping(value = {"/setAccessCode/kakao/{accessCode}","/setAccessCode/kakao/"})
    public ResponseEntity<?> setAccessCode(@PathVariable String accessCode, HttpServletResponse response) throws Exception {
        log.info("accessCode : {}", accessCode);
        String accessToken = kakaoService.getKakaoAccessToken(accessCode);
        log.info("accessToken : {}", accessToken);
        OAuthDTO oAuthDTO = kakaoService.createKakaoUser(accessToken);
        String kakaoPhoneNo = oAuthDTO.getPhoneNo();

        // TODO : 카카오 에러 // 1000
        if(kakaoPhoneNo.equals(""))
            return ErrorReturn(ApiCode.UNKNOWN_ERROR);

        // TODO : 카카오 전화번호 X
        if(kakaoPhoneNo.equals("PhoneNoNotExist"))
            return ErrorReturn(ApiCode.KAKAO_PHONE_NO_NOT_EXIST);

        kakaoPhoneNo = kakaoPhoneNo.replaceAll("[^0-9]", "");
        kakaoPhoneNo = "010" + kakaoPhoneNo.substring(kakaoPhoneNo.length()-8, kakaoPhoneNo.length());
        oAuthDTO.setPhoneNo(kakaoPhoneNo);
        log.info("kakaoPhoneNo : {}", kakaoPhoneNo);

        // TODO : 카카오 전화번호 0 ,  DB 전화번호 X // PhoneNo 반환
        if(!userService.isPhoneNoExisting(kakaoPhoneNo))
            return ErrorReturn(ApiCode.PHONE_NO_NOT_EXIST, oAuthDTO);

        User user = userService.getUserByPhoneNo(kakaoPhoneNo);
        oAuthDTO.setUserSocial(user.getUserSocial());

        // TODO : 카카오 전화번호 0 , DB 전화번호 0, 카카오 연동 X // 현재 연동된 OAuth 코드 반환
        oAuthDTO.setEmail(user.getEmail());
        if(user.getUserSocial() != UserSocial.K)
            return ErrorReturn(ApiCode.KAKAO_NOT_CONNECT, oAuthDTO);

        Pet pet = petService.getPetByUserId(user.getUserId());
        String Access_jwtToken = tokenService.createToken("Access", user.getEmail()); // Access Token 생성
        String Refresh_jwtToken = tokenService.createToken("Refresh", user.getEmail()); // Refresh Token 생성

        response.addHeader(Access_JwtProperties.HEADER_STRING, Access_JwtProperties.TOKEN_PREFIX + Access_jwtToken);
        response.addHeader(Refresh_JwtProperties.HEADER_STRING, Refresh_JwtProperties.TOKEN_PREFIX + Refresh_jwtToken);

        return SuccessReturn(new UserPetDTO(user, pet)); //200
    }
}
