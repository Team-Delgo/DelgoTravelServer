package com.delgo.api.controller;

import com.delgo.api.comm.ncp.service.SmsService;
import com.delgo.api.comm.security.services.PrincipalDetails;
import com.delgo.api.repository.UserRepository;
import com.delgo.api.service.CouponService;
import com.delgo.api.service.TestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {

    private final TestService testService;
    private final CouponService couponService;
    private final UserRepository userRepository;
    private final SmsService smsService;
    private final MessageSource ms;


    @GetMapping("/api/home")
    public String home() {
        return "<h1>home</h1>";
    }

    // 유저 혹은 매니저 혹은 어드민이 접근 가능
    @GetMapping("/login")
    public String user(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        System.out.println("principal : " + principal.getUser().getUserId());
        System.out.println("principal : " + principal.getUser().getEmail());
        System.out.println("principal : " + principal.getUser().getPassword());

        return "<h1>user</h1>";
    }

    @GetMapping("/test")
    public void test() {
        couponService.getCouponByCouponId(4);
    }


//    /**
//     * 예약 대기 문자
//     **/
//    @GetMapping("/test")
//    public void test(String text) throws UnsupportedEncodingException, NoSuchAlgorithmException, URISyntaxException, InvalidKeyException, JsonProcessingException {
//        /**
//         ▶숙소이름 : {0}
//         ▶객실타입 : {1} ex) (NNNNN (기준인원2명) )
//         ▶견종타입 : {2}
//         ▶추가사항 : {3} ex) 추가인원 1명 , 추가견 1마리
//         ▶입실일시 : {4} ex) 06.06 (수) 15:00 ~
//         ▶퇴실일시 : {5} ex) 06:07 (목) 11:00
//         */
//        new
//        String result = ms.getMessage("notification.wait", new Object[]{"Spring", "asdf"}, null);
//        log.info(result);
////        smsService.sendSMS("01062511583",result);
////        return user;
//    }
}
