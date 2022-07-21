package com.delgo.api.service;

import com.delgo.api.comm.exception.ApiCode;
import com.delgo.api.comm.ncp.service.SmsService;
import com.delgo.api.domain.SmsAuth;
import com.delgo.api.repository.SmsAuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SmsAuthService {
    private final SmsService smsService;
    private final SmsAuthRepository smsAuthRepository;

    // 인증번호 발송
    public int sendSMS(String phoneNo) {
        Random rand = new Random();
        String randNum = "";
        for (int i = 0; i < 4; i++) {
            String ran = Integer.toString(rand.nextInt(10));
            randNum += ran;
        }
        String message = "[Delgo] 인증번호 " + randNum;

        try {
            smsService.sendSMS(phoneNo, message);
            String authTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            SmsAuth smsAuth = SmsAuth.builder()
                    .randNum(randNum)
                    .phoneNo(phoneNo)
                    .build();
            smsAuthRepository.save(smsAuth);
            int smsId = smsAuth.getSmsId();
            return smsId;
        } catch (Exception e) {
            throw new IllegalStateException();
        }
    }

    // 인증번호 확인
    public void checkSMS(int smsId, String enterNum) {
        Optional<SmsAuth> findSmsAuth = smsAuthRepository.findBySmsId(smsId);
        if (!findSmsAuth.get().getRandNum().equals(enterNum)) {
            log.warn("The authentication numbers do not match");
            throw new IllegalStateException();
        }
        LocalDateTime sendTime = findSmsAuth.get().getAuthTime();
        LocalDateTime authTime = LocalDateTime.now();
        Long effectTime = ChronoUnit.MINUTES.between(sendTime, authTime);
        if (effectTime > 3)
            throw new IllegalStateException();
    }
    public SmsAuth getSmsAuthByPhoneNo(String phoneNO) {
        return smsAuthRepository.findByPhoneNo(phoneNO)
                .orElseThrow(() -> new NullPointerException("NOT FOUND SMS AUTH DATA"));
    }
    public boolean isAuth(SmsAuth smsAuth){
        LocalDateTime sendTime = smsAuth.getAuthTime();
        LocalDateTime authTime = LocalDateTime.now();
        Long effectTime = ChronoUnit.MINUTES.between(sendTime, authTime);
        if (effectTime < 10)
            return true;

        return false;
    }
}
