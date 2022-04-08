package com.delgo.api.service;

import com.delgo.api.domain.pet.Pet;
import com.delgo.api.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;

    public Pet findByUserId(int userId) {
        return petRepository.findByUserId(userId)
                .orElseThrow(()->new NullPointerException("Not Found PetData"));
    }

}
