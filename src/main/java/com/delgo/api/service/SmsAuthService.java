package com.delgo.api.service;

import com.delgo.api.comm.CommService;
import com.delgo.api.comm.exception.ApiCode;
import com.delgo.api.comm.ncp.service.SmsService;
import com.delgo.api.domain.SmsAuth;
import com.delgo.api.repository.SmsAuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SmsAuthService extends CommService {
    private final SmsService smsService;
    private final SmsAuthRepository smsAuthRepository;

    public boolean isSmsAuthExisting(String phoneNo) {
        return smsAuthRepository.findByPhoneNo(phoneNo).isPresent();
    }

    public int makeAuth(String phoneNo){
        String randNum = numberGen(4, 1);
        String message = "[Delgo] 인증번호 " + randNum;

        try{
            smsService.sendSMS(phoneNo, message);
        } catch (Exception e){
            throw new IllegalStateException();
        }

        return smsAuthRepository.save(
                isSmsAuthExisting(phoneNo)
                        ? getSmsAuthByPhoneNo(phoneNo).setRandNum(randNum)
                        : SmsAuth.builder().randNum(randNum).phoneNo(phoneNo).build()).getSmsId();
    }

    // 인증번호 발송 && CREATE
//    public int createSmsAuth(String phoneNo) {
//        String randNum = numberGen(4, 1);
//        String message = "[Delgo] 인증번호 " + randNum;
//        try {
//            smsService.sendSMS(phoneNo, message);
//            SmsAuth smsAuth = SmsAuth.builder().randNum(randNum).phoneNo(phoneNo).build();
//            smsAuthRepository.save(smsAuth);
//
//            int smsId = smsAuth.getSmsId();
//            return smsId;
//        } catch (Exception e) {
//            throw new IllegalStateException();
//        }
//    }

    // 인증번호 발송 && UPDATE
//    public int updateSmsAuth(String phoneNo) {
//        String randNum = numberGen(4, 1);
//        String message = "[Delgo] 인증번호 " + randNum;
//        try {
//            smsService.sendSMS(phoneNo, message);
//            SmsAuth smsAuth = smsAuthRepository.findByPhoneNo(phoneNo).get();
//            smsAuth.setAuthTime(LocalDateTime.now());
//            smsAuth.setRandNum(randNum);
//            smsAuthRepository.save(smsAuth);
//
//            int smsId = smsAuth.getSmsId();
//            return smsId;
//        } catch (Exception e) {
//            throw new IllegalStateException();
//        }
//    }

    // 인증번호 확인
    public Optional<ApiCode> checkSMS(int smsId, String enterNum) {
        SmsAuth findSmsAuth = getSmsAuthBySmsId(smsId);
        if (!findSmsAuth.getRandNum().equals(enterNum)) {
            log.warn("The authentication numbers do not match");
            return Optional.of(ApiCode.AUTH_DO_NOT_MATCHING);
        }

        if (ChronoUnit.MINUTES.between(findSmsAuth.getAuthTime(), LocalDateTime.now()) > 3)
            return Optional.of(ApiCode.AUTH_DO_NOT_MATCHING);

        return Optional.empty();
    }

    public SmsAuth getSmsAuthByPhoneNo(String phoneNO) {
        return smsAuthRepository.findByPhoneNo(phoneNO)
                .orElseThrow(() -> new NullPointerException("NOT FOUND SMS AUTH DATA"));
    }

    public SmsAuth getSmsAuthBySmsId(int smsId) {
        return smsAuthRepository.findBySmsId(smsId)
                .orElseThrow(() -> new NullPointerException("NOT FOUND SMS AUTH DATA"));
    }

    public boolean isAuth(SmsAuth smsAuth) {
        return (ChronoUnit.MINUTES.between(smsAuth.getAuthTime(), LocalDateTime.now()) < 10);
    }
}
