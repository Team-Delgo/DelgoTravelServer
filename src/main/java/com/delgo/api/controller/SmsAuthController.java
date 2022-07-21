package com.delgo.api.controller;

import com.delgo.api.comm.CommController;
import com.delgo.api.comm.exception.ApiCode;
import com.delgo.api.service.SmsAuthService;
import com.delgo.api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SmsAuthController extends CommController {
    private final UserService userService;
    private final SmsAuthService smsAuthService;

    // 회원가입 전 인증번호 전송
    @GetMapping("/phoneNoAuth")
    public ResponseEntity<?> phoneNoCheck(@RequestParam String phoneNo) {
        try {
            if(phoneNo.isBlank()){
                return ErrorReturn(ApiCode.PARAM_ERROR);
            }
            phoneNo = phoneNo.replaceAll("[^0-9]", "");

            if(userService.isPhoneNoExisting(phoneNo)){
                return ErrorReturn(ApiCode.PHONE_NO_DUPLICATE_ERROR);
            } else {
                int smsId = smsAuthService.sendSMS(phoneNo);
                return SuccessReturn(smsId);
            }
        } catch (Exception e){
            return ErrorReturn(ApiCode.UNKNOWN_ERROR);
        }
    }

    // 회원가입 후 인증번호 전송
    @GetMapping("/phoneNoCheck")
    public ResponseEntity<?> phoneNoAuth(@RequestParam String phoneNo) {
        try {
            if(phoneNo.isBlank()){
                return ErrorReturn(ApiCode.PARAM_ERROR);
            }
            phoneNo = phoneNo.replaceAll("[^0-9]", "");
            if(!userService.isPhoneNoExisting(phoneNo)){
                return ErrorReturn(ApiCode.PHONE_NO_NOT_EXIST);
            } else {
                int smsId = smsAuthService.sendSMS(phoneNo);
                return SuccessReturn(smsId);
            }
        } catch (Exception e){
            return ErrorReturn(ApiCode.UNKNOWN_ERROR);
        }
    }

    // 인증번호 확인
    @GetMapping("/authRandNum")
    public ResponseEntity<?> randNumCheck(@RequestParam Integer smsId, @RequestParam String enterNum) {
        if(enterNum.isBlank()){
            return ErrorReturn(ApiCode.PARAM_ERROR);
        }
        smsAuthService.checkSMS(smsId, enterNum);
        return SuccessReturn();
    }
}