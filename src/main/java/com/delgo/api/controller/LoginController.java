package com.delgo.api.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.delgo.api.domain.pet.Pet;
import com.delgo.api.domain.user.User;
import com.delgo.api.dto.UserDTO;
import com.delgo.api.dto.common.ResponseDTO;
import com.delgo.api.security.jwt.Access_JwtProperties;
import com.delgo.api.security.jwt.Refresh_JwtProperties;
import com.delgo.api.service.PetService;
import com.delgo.api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;
    private final PetService petService;

    final String ACCESS = "Access";
    final String REFRESH = "Refresh";

    @PostMapping("/loginSuccess")
    public ResponseEntity<?> loginSuccess(HttpServletRequest request, HttpServletResponse response) {
        String email = request.getAttribute("email").toString();

        User user = userService.findByEmail(email);
        Pet pet = petService.findByUserId(user.getUserId());

        String Access_jwtToken = createToken(ACCESS, email); // Access Token 생성
        String Refresh_jwtToken = createToken(REFRESH, email); // Refresh Token 생성

        response.addHeader(Access_JwtProperties.HEADER_STRING, Access_JwtProperties.TOKEN_PREFIX + Access_jwtToken);
        response.addHeader(Refresh_JwtProperties.HEADER_STRING, Refresh_JwtProperties.TOKEN_PREFIX + Refresh_jwtToken);

        ResponseDTO responseDTO = ResponseDTO.builder().code(200).codeMsg("success").data(new UserDTO(user, pet)).build();
        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping("/loginFail")
    public ResponseEntity<?> loginFail() {
        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(303).codeMsg("fail").build()
        );
    }

    @GetMapping("/tokenError")
    public ResponseEntity<?> tokenError() {
        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(304).codeMsg("Token Certification failed").build()
        );
    }

    @GetMapping("/tokenReissue")
    public ResponseEntity<?> tokenReissue(HttpServletRequest request, HttpServletResponse response) {
        try {
            String token = request.getHeader(Refresh_JwtProperties.HEADER_STRING)
                    .replace(Refresh_JwtProperties.TOKEN_PREFIX, "");
            String email = JWT.require(Algorithm.HMAC512(Refresh_JwtProperties.SECRET)).build().verify(token)
                    .getClaim("email").asString();

            String Access_jwtToken = createToken(ACCESS, email); // Access Token 생성
            String Refresh_jwtToken = createToken(REFRESH, email); // Refresh Token 생성

            response.addHeader(Access_JwtProperties.HEADER_STRING, Access_JwtProperties.TOKEN_PREFIX + Access_jwtToken);
            response.addHeader(Refresh_JwtProperties.HEADER_STRING, Refresh_JwtProperties.TOKEN_PREFIX + Refresh_jwtToken);

            return ResponseEntity.ok().body(
                    ResponseDTO.builder().code(200).codeMsg("Token tokenReissue success").build()
            );
        } catch (Exception e) { // Refresh_Toekn 인증 실패 ( 로그인 화면으로 이동 필요 )
            return ResponseEntity.ok().body(
                    ResponseDTO.builder().code(305).codeMsg("Refresh_Token Certification failed").build()
            );
        }
    }

    @GetMapping("/api/test")
    public ResponseEntity<?> test() {
        System.out.printf("test 들어옴");
        ResponseDTO responseDTO = ResponseDTO.builder().code(200).codeMsg("테스트 들어옴 ~~~~").build(); // code는 논의 필요
        return ResponseEntity.ok().body(responseDTO);
    }

    // Create Token
    public String createToken(String tokenType, String email) {
        if (tokenType.equals(ACCESS)) // Access Token
            return JWT.create()
                    .withSubject(email)
                    .withExpiresAt(new Date(System.currentTimeMillis() + Access_JwtProperties.EXPIRATION_TIME))
                    .withClaim("email", email)// getUsername() == getEmail()
                    .sign(Algorithm.HMAC512(Access_JwtProperties.SECRET));
        else // Refresh Token
            return JWT.create()
                    .withSubject(email)
                    .withExpiresAt(new Date(System.currentTimeMillis() + Refresh_JwtProperties.EXPIRATION_TIME))
                    .withClaim("email", email)// getUsername() == getEmail()
                    .sign(Algorithm.HMAC512(Refresh_JwtProperties.SECRET));
    }
}
