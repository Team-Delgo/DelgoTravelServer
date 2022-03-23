package com.delgo.api.service;

import com.delgo.api.domain.user.User;
import com.delgo.api.dto.UserDTO;
import com.delgo.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User create(UserDTO userDTO){
        validateDuplicate(userDTO);
        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
        userDTO.setPassword(encodedPassword);
        userRepository.save(userDTO.toEntity());
        return userRepository.findByEmail(userDTO.getEmail());
    }

    private void validateDuplicate(UserDTO userDTO){
        User findUser = userRepository.findByEmail(userDTO.getEmail());
        if(findUser != null){
            log.warn("Email already exists {}", findUser.getEmail());
            throw new IllegalStateException("이미 가입된 이메일입니다.");
        }
    }

}
