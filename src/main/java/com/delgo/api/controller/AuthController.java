package com.delgo.api.controller;

import com.delgo.api.comm.CommController;
import com.delgo.api.comm.exception.ApiCode;
import com.delgo.api.domain.user.User;
import com.delgo.api.domain.user.UserSocial;
import com.delgo.api.service.SmsAuthService;
import com.delgo.api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController extends CommController {
    private final UserService userService;
    private final SmsAuthService smsAuthService;

    // 이메일 존재 유무 확인
    @GetMapping("/email")
    public ResponseEntity<?> emailAuth(@RequestParam String email) {
        if (email.isBlank()) {
            return ErrorReturn(ApiCode.PARAM_ERROR);
        }

        if (userService.isEmailExisting(email)) {
            User user = userService.getUserByEmail(email).makeEmpty();
            return SuccessReturn(user);
//            return SuccessReturn(userService.getUserByEmail(email).getPhoneNo());
        }
        return ErrorReturn(ApiCode.EMAIL_NOT_EXIST);
    }

    // 이메일 중복 확인
    @GetMapping("/email/check")
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
    @GetMapping("/name/check")
    public ResponseEntity<?> nameCheck(@RequestParam String name) {
        if (name.isBlank()) {
            return ErrorReturn(ApiCode.PARAM_ERROR);
        }
        if (!userService.isNameExisting(name))
            return SuccessReturn();
        else
            return ErrorReturn(ApiCode.NAME_DUPLICATE_ERROR);
    }

    // 인증번호 전송
    @GetMapping("/sms")
    public ResponseEntity<?> phoneNoAuth(@RequestParam String phoneNo, @RequestParam boolean isJoin) {
        try {
            if (phoneNo.isBlank()) {
                return ErrorReturn(ApiCode.PARAM_ERROR);
            }
            phoneNo = phoneNo.replaceAll("[^0-9]", "");

            if (isJoin && !userService.isPhoneNoExisting(phoneNo)) return ErrorReturn(ApiCode.PHONE_NO_NOT_EXIST);
            if (!isJoin && userService.isPhoneNoExisting(phoneNo)) return ErrorReturn(ApiCode.PHONE_NO_DUPLICATE_ERROR);

            return SuccessReturn(smsAuthService.makeAuth(phoneNo));
        } catch (Exception e) {
            return ErrorReturn(ApiCode.UNKNOWN_ERROR);
        }
//            if (userService.isPhoneNoExisting(phoneNo)) {
//                User user = userService.getUserByPhoneNo(phoneNo);
//                Map<String, Object> returnMap = new HashMap<>();
//                if (user.getUserSocial().equals(UserSocial.A))
//                    returnMap.put("email", "애플로 로그인해주세요.");
//                else
//                    returnMap.put("email", user.getEmail());
//                returnMap.put("userSocial", user.getUserSocial());
//                return ErrorReturn(ApiCode.PHONE_NO_DUPLICATE_ERROR, returnMap);
//            }
//
//            if (smsAuthService.isSmsAuthExisting(phoneNo)) {
//                int smsId = smsAuthService.updateSmsAuth(phoneNo);
//                return SuccessReturn(smsId);
//            } else {
//                int smsId = smsAuthService.createSmsAuth(phoneNo);
//                return SuccessReturn(smsId);
//            }
//
//        } catch (Exception e) {
//            return ErrorReturn(ApiCode.UNKNOWN_ERROR);
//        }
    }

    // 인증번호 확인
    @GetMapping("/sms/check")
    public ResponseEntity<?> randNumCheck(@RequestParam Integer smsId, @RequestParam String enterNum) {
        if (enterNum.isBlank()) {
            return ErrorReturn(ApiCode.PARAM_ERROR);
        }
        Optional<ApiCode> apiCode = smsAuthService.checkSMS(smsId, enterNum);
        if (apiCode.isPresent())
            return ErrorReturn(apiCode.get());

        return SuccessReturn();
    }



    // 회원가입 후 인증번호 전송
//    @GetMapping("/phoneNoCheck")
//    public ResponseEntity<?> phoneNoCheck(@RequestParam String phoneNo) {
//        try {
//            if (phoneNo.isBlank()) {
//                return ErrorReturn(ApiCode.PARAM_ERROR);
//            }
//            phoneNo = phoneNo.replaceAll("[^0-9]", "");
//            if (!userService.isPhoneNoExisting(phoneNo)) {
//                return ErrorReturn(ApiCode.PHONE_NO_NOT_EXIST);
//            }
//            if (smsAuthService.isSmsAuthExisting(phoneNo)) {
//                int smsId = smsAuthService.updateSmsAuth(phoneNo);
//                return SuccessReturn(smsId);
//            } else {
//                int smsId = smsAuthService.createSmsAuth(phoneNo);
//                return SuccessReturn(smsId);
//            }
//        } catch (Exception e) {
//            return ErrorReturn(ApiCode.UNKNOWN_ERROR);
//        }
//    }

    // 인증번호 확인
//    @GetMapping("/authRandNum")
//    public ResponseEntity<?> randNumCheck(@RequestParam Integer smsId, @RequestParam String enterNum) {
//        if (enterNum.isBlank()) {
//            return ErrorReturn(ApiCode.PARAM_ERROR);
//        }
//        Optional<ApiCode> apiCode = smsAuthService.checkSMS(smsId, enterNum);
//        if (apiCode.isPresent())
//            return ErrorReturn(apiCode.get());
//
//        return SuccessReturn();
//    }

}
