package com.delgo.api.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.delgo.api.comm.CommController;
import com.delgo.api.comm.exception.ApiCode;
import com.delgo.api.comm.security.jwt.Access_JwtProperties;
import com.delgo.api.comm.security.jwt.Refresh_JwtProperties;
import com.delgo.api.domain.coupon.Coupon;
import com.delgo.api.domain.pet.Pet;
import com.delgo.api.domain.user.User;
import com.delgo.api.service.CouponService;
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
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LoginController extends CommController {

    private final UserService userService;
    private final PetService petService;
    private final CouponService couponService;
    private final TokenService tokenService;

    final String ACCESS = "Access";
    final String REFRESH = "Refresh";
    final String EMAIL = "email";

    /*
     * Login 성공
     * Header [ Access_Token, Refresh_Token ]
     * Body [ User , Pet ]
     * 담아서 반환한다.
     */
    @PostMapping("/loginSuccess")
    public ResponseEntity<?> loginSuccess(HttpServletRequest request, HttpServletResponse response) {
        String email = request.getAttribute(EMAIL).toString();


        User user = userService.getUserByEmail(email);
//        user.setPassword(""); // TODO: 이거 키면 비밀번호가 사라짐 왜그런지 찾아볼 것
        Pet pet = petService.getPetByUserId(user.getUserId());
        List<Coupon> couponList = couponService.getCouponsByUserId(user.getUserId());

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("pet", pet);
        map.put("couponList", couponList);
        map.put("user", user);

        String Access_jwtToken = tokenService.createToken(ACCESS, email); // Access Token 생성
        String Refresh_jwtToken = tokenService.createToken(REFRESH, email); // Refresh Token 생성

        response.addHeader(Access_JwtProperties.HEADER_STRING, Access_JwtProperties.TOKEN_PREFIX + Access_jwtToken);
        response.addHeader(Refresh_JwtProperties.HEADER_STRING, Refresh_JwtProperties.TOKEN_PREFIX + Refresh_jwtToken);

        return SuccessReturn(map);
    }

    /*
     * Login 실패
     * ErrorCode 반환.
     */

    @PostMapping("/loginFail")
    public ResponseEntity<?> loginFail() {
        return ErrorReturn(ApiCode.LOGIN_ERROR);
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
                    .getClaim(EMAIL).asString();

            String Access_jwtToken = tokenService.createToken(ACCESS, email); // Access Token 생성
            String Refresh_jwtToken = tokenService.createToken(REFRESH, email); // Refresh Token 생성

            response.addHeader(Access_JwtProperties.HEADER_STRING, Access_JwtProperties.TOKEN_PREFIX + Access_jwtToken);
            response.addHeader(Refresh_JwtProperties.HEADER_STRING, Refresh_JwtProperties.TOKEN_PREFIX + Refresh_jwtToken);

            return SuccessReturn();
        } catch (Exception e) { // Refresh_Toekn 인증 실패 ( 로그인 화면으로 이동 필요 )
            return ErrorReturn(ApiCode.REFRESH_TOKEN_ERROR);
        }
    }

    /*
     * TOKEN 인증 프로세스중 에러 발생
     * ErrorCode 반환.
     */
    @RequestMapping("/tokenError")
    public ResponseEntity<?> tokenError() {
        return ErrorReturn(ApiCode.TOKEN_ERROR);
    }
}
