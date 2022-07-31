package com.delgo.api.service;

import com.delgo.api.domain.booking.Booking;
import com.delgo.api.domain.booking.BookingState;
import com.delgo.api.domain.pet.Pet;
import com.delgo.api.domain.user.User;
import com.delgo.api.dto.user.InfoDTO;
import com.delgo.api.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final CouponRepository couponRepository;
    private final ReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder;
    private final BookingRepository bookingRepository;

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
    public void deleteUser(int userId) {
        User user = userRepository.findByUserId(userId).orElseThrow(() -> new NullPointerException("NOT FOUND USER"));
        Pet pet = petRepository.findByUserId(userId).orElseThrow(() -> new NullPointerException("NOT FOUND PET"));
        petRepository.delete(pet);
        userRepository.delete(user);
    }

    // 비밀번호 변경
    public void changePassword(String checkedEmail, String newPassword) {
        User user = userRepository.findByEmail(checkedEmail).orElseThrow(() -> new IllegalArgumentException("The " +
                "email does not exist"));
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);
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

    // 이름 존재 유무 확인
    public boolean isNameExisting(String name) {
        Optional<User> findUser = userRepository.findByName(name);
        return findUser.isPresent();
    }

    // myAccount
    public InfoDTO getInfoByUserId(int userId) {
        User user = userRepository.findByUserId(userId).orElseThrow(() -> new NullPointerException("NOT FOUND USER"));
        String profileUrl = user.getProfile();

        Pet pet = petRepository.findByUserId(userId).orElseThrow(() -> new NullPointerException("NOT FOUND PET"));

        String petName = pet.getName();

        int couponNum = couponRepository.findByUserId(userId).size();
        int reviewNum = reviewRepository.findByUserId(userId).size();

        List<Booking> waitBookingList = bookingRepository.findByUserIdAndBookingState(userId, BookingState.W);
        List<Booking> fixBookingList = bookingRepository.findByUserIdAndBookingState(userId, BookingState.F);
        List<Booking> bookingList = Stream.concat(waitBookingList.stream(), fixBookingList.stream()).collect(Collectors.toList());


        InfoDTO infoDTO = new InfoDTO(profileUrl, petName, couponNum, reviewNum,bookingList);

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
