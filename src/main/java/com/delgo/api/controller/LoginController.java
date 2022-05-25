package com.delgo.api.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.delgo.api.config.security.jwt.Access_JwtProperties;
import com.delgo.api.config.security.jwt.Refresh_JwtProperties;
import com.delgo.api.domain.pet.Pet;
import com.delgo.api.domain.user.User;
import com.delgo.api.dto.UserDTO;
import com.delgo.api.dto.common.ResponseDTO;
import com.delgo.api.service.PetService;
import com.delgo.api.service.TokenService;
import com.delgo.api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;
    private final PetService petService;
    private final TokenService tokenService;

    final String ACCESS = "Access";
    final String REFRESH = "Refresh";

    /*
     * Login 성공
     * Header [ Access_Token, Refresh_Token ]
     * Body [ User , Pet ]
     * 담아서 반환한다.
     */
    @PostMapping("/loginSuccess")
    public ResponseEntity<?> loginSuccess(HttpServletRequest request, HttpServletResponse response) {
        String email = request.getAttribute("email").toString();

        User user = userService.findByEmail(email);
        Pet pet = petService.findByUserId(user.getUserId());

        String Access_jwtToken = tokenService.createToken(ACCESS, email); // Access Token 생성
        String Refresh_jwtToken = tokenService.createToken(REFRESH, email); // Refresh Token 생성

        response.addHeader(Access_JwtProperties.HEADER_STRING, Access_JwtProperties.TOKEN_PREFIX + Access_jwtToken);
        response.addHeader(Refresh_JwtProperties.HEADER_STRING, Refresh_JwtProperties.TOKEN_PREFIX + Refresh_jwtToken);

        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(200).codeMsg("Login Success").data(new UserDTO(user, pet)).build());
    }

    /*
     * Access_Token 재발급 API
     * Refresh_Token 인증 진행
     * 성공 : 재발급, 실패 : 오류 코드 반환
     */
    @GetMapping("/tokenReissue")
    public ResponseEntity<?> tokenReissue(HttpServletRequest request, HttpServletResponse response) {
        try {
            String token = request.getHeader(Refresh_JwtProperties.HEADER_STRING)
                    .replace(Refresh_JwtProperties.TOKEN_PREFIX, "");
            String email = JWT.require(Algorithm.HMAC512(Refresh_JwtProperties.SECRET)).build().verify(token)
                    .getClaim("email").asString();

            String Access_jwtToken = tokenService.createToken(ACCESS, email); // Access Token 생성
            String Refresh_jwtToken = tokenService.createToken(REFRESH, email); // Refresh Token 생성

            response.addHeader(Access_JwtProperties.HEADER_STRING, Access_JwtProperties.TOKEN_PREFIX + Access_jwtToken);
            response.addHeader(Refresh_JwtProperties.HEADER_STRING, Refresh_JwtProperties.TOKEN_PREFIX + Refresh_jwtToken);

            return ResponseEntity.ok().body(
                    ResponseDTO.builder().code(200).codeMsg("Token tokenReissue success").build());
        } catch (Exception e) { // Refresh_Toekn 인증 실패 ( 로그인 화면으로 이동 필요 )
            return ResponseEntity.ok().body(
                    ResponseDTO.builder().code(305).codeMsg("Refresh_Token Certification failed").build());
        }
    }

    @PostMapping("/loginFail")
    public ResponseEntity<?> loginFail() {
        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(303).codeMsg("Login Fail").build());
    }

    @RequestMapping("/tokenError")
    public ResponseEntity<?> getTokenError() {
        log.info("tokenError");
        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(304).codeMsg("Token Certification failed").build());
    }

    @GetMapping("/api/test")
    public ResponseEntity<?> test() {
        System.out.printf("test 들어옴");
        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(200).codeMsg("테스트 들어옴 ~~~~").build());
    }

}
