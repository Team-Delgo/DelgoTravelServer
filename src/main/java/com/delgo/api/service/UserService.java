package com.delgo.api.service;

import com.delgo.api.domain.user.User;
import com.delgo.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private final UserRepository userRepository;

    public User create(User user){
        validateDuplicate(user);
        return userRepository.save(user);
    }

    private void validateDuplicate(User user){
        User findUser = userRepository.findByEmail(user.getEmail());
        if(findUser != null){
            log.warn("Email already exists {}", findUser.getEmail());
            throw new IllegalStateException("이미 가입된 이메일입니다.");
        }
    }

}
