package com.delgo.api.service;

import com.delgo.api.domain.pet.Pet;
import com.delgo.api.domain.user.User;
import com.delgo.api.dto.UserDTO;
import com.delgo.api.repository.PetRepository;
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
    private final PetRepository petRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User create(UserDTO userDTO){
        validateDuplicate(userDTO.getEmail());
        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
        userDTO.setPassword(encodedPassword);
        userRepository.save(userDTO.toEntity());
        Pet pet = userDTO.getPet();
        User owner = userRepository.findByEmail(userDTO.getEmail());
        int owner_id = owner.getUser_id();
        petRepository.save(Pet.builder().user_id(owner_id).name(pet.getName()).size(pet.getSize()).birthday(pet.getBirthday()).build());
        return userRepository.findByEmail(userDTO.getEmail());
    }

    public void validateDuplicate(String email) {
        User findUser = userRepository.findByEmail(email);
        if(findUser != null){
            System.out.println(findUser.getEmail());
            log.warn("Email already exists {}", findUser.getEmail());
            throw new IllegalStateException("Email is duplicate");
        }
    }

    public User findByEmail(String email){
        return userRepository.findByEmail(email);
    }

}
