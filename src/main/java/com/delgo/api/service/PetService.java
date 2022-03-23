package com.delgo.api.service;

import com.delgo.api.domain.pet.Pet;
import com.delgo.api.domain.user.User;
import com.delgo.api.dto.PetDTO;
import com.delgo.api.repository.PetRepository;
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
public class PetService {
    @Autowired
    private PetRepository petRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Pet create(int user_id, PetDTO petDTO){
        User owner = userRepository.findById(user_id);
        petDTO.setUser(owner);
        petRepository.save(petDTO.toEntity()).getPet_id();
        return petRepository.findByName(petDTO.getName());
    }


}
