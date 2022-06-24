package com.delgo.api.service;

import com.delgo.api.comm.ncp.service.SmsService;
import com.delgo.api.domain.SmsAuth;
import com.delgo.api.domain.pet.Pet;
import com.delgo.api.domain.user.User;
import com.delgo.api.dto.InfoDTO;
import com.delgo.api.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final CouponRepository couponRepository;
    private final ReviewRepository reviewRepository;
    private final SmsAuthRepository smsAuthRepository;
    private final PasswordEncoder passwordEncoder;
    private final SmsService smsService;

    // 회원가입
    public User signup(User user, Pet pet) {
        // Email 중복확인
        isEmailExisting(user.getEmail());
        // 패스워드 암호화 및 적용
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        // User Data save
        User owner = userRepository.save(user);
        // Pet Data save
        pet.setUserId(owner.getUserId());
        petRepository.save(pet);

        return owner;
    }

    // 소셜 회원가입
    public User socialSignup(User user, Pet pet) {
        // User Data save
        user.setPassword("");
        User owner = userRepository.save(user);
        // Pet Data save
        pet.setUserId(owner.getUserId());
        petRepository.save(pet);

        return owner;
    }

    // 회원탈퇴
    public void deleteUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("The email does not exist"));
        Pet pet = petRepository.findByUserId(user.getUserId()).orElseThrow(() -> new IllegalArgumentException("Something wrong"));
        petRepository.delete(pet);
        userRepository.delete(user);
    }

    // 비밀번호 변경
    public void changePassword(String checkedEmail, String newPassword) {
        User user = userRepository.findByEmail(checkedEmail).orElseThrow(() -> new IllegalArgumentException("The email does not exist"));
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }

    // 인증번호 발송
    public int sendSMS(String phoneNo) throws UnsupportedEncodingException, NoSuchAlgorithmException, URISyntaxException, InvalidKeyException, JsonProcessingException {
        Random rand = new Random();
        String randNum = "";
        for (int i = 0; i < 4; i++) {
            String ran = Integer.toString(rand.nextInt(10));
            randNum += ran;
        }
        String message = "[Delgo] 인증번호 " + randNum;
        SmsAuth smsAuth = new SmsAuth();
        try {
            smsService.sendSMS(phoneNo, message);
            smsAuth.setRandNum(randNum);
            smsAuthRepository.save(smsAuth);
        } catch (Exception e) {
            throw new IllegalStateException();
        }
        int smsId = smsAuth.getSmsId();
        return smsId;
    }

    // 인증번호 확인
    public void checkSMS(int smsId, String enterNum) {
        Optional<SmsAuth> findSmsAuth = smsAuthRepository.findBySmsId(smsId);
        if (!findSmsAuth.get().getRandNum().equals(enterNum)) {
            log.warn("The authentication numbers do not match");
            throw new IllegalStateException();
        }
        smsAuthRepository.delete(findSmsAuth.get());
    }

    // 전화번호 존재 유무 확인
    public boolean isPhoneNoExisting(String phoneNo) {
        Optional<User> findUser = userRepository.findByPhoneNo(phoneNo);
        return findUser.isPresent();
    }

    // 이메일 존재 유무 확인
    public boolean isEmailExisting(String email) {
        Optional<User> findUser = userRepository.findByEmail(email);
        return findUser.isPresent();
    }

    // myAccount
    public InfoDTO getInfoByUserId(int userId) throws IOException {
        User user = userRepository.findByUserId(userId).orElseThrow(() -> new IllegalStateException("Not Found UserData"));
        String profileUrl = user.getProfile();

        Pet pet = petRepository.findByUserId(userId).orElseThrow(() -> new IllegalStateException("Not Found PetData"));

        String petName = pet.getName();

        int couponNum = couponRepository.findByUserId(userId).size();
        int reviewNum = reviewRepository.findByUserId(userId).size();

        InfoDTO infoDTO = new InfoDTO(profileUrl, petName, couponNum, reviewNum);

        return infoDTO;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NullPointerException("NOT FOUND USER"));
    }

    public User getUserByUserId(int userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new NullPointerException("NOT FOUND USER"));
    }

    public User updateUserData(User user) {
        return userRepository.save(user);
    }

}
