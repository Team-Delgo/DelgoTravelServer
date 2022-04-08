package com.delgo.api.service;

import com.delgo.api.domain.pet.Pet;
import com.delgo.api.domain.user.User;
import com.delgo.api.repository.PetRepository;
import com.delgo.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void create(User user, Pet pet) {
        // Email 중복확인
        validateDuplicate(user.getEmail());
        // 패스워드 암호화 및 적용
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        // User Data save
        User owner = userRepository.save(user);
        // Pet Data save
        pet.setUserId(owner.getUserId());
        petRepository.save(pet);
    }


    public void validateDuplicate(String email) {
        Optional<User> findUser = userRepository.findByEmail(email);
        if(findUser.isPresent()){
            log.warn("Email already exists {}", findUser.get().getEmail());
            throw new IllegalStateException("Email is duplicate");
        }
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Not Found UserData"));
    }

}
