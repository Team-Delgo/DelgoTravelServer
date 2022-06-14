package com.delgo.api.service;

import com.delgo.api.domain.pet.Pet;
import com.delgo.api.domain.pet.PetSize;
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

    @Transactional
    public void changePetInfo(Pet pet, String birthday, String name, PetSize size, double weight){
        pet.setBirthday(birthday);
        pet.setName(name);
        pet.setSize(size);
        pet.setWeight(weight);
        petRepository.save(pet);
    }

    public Pet getPetByUserId(int userId) {
        return petRepository.findByUserId(userId)
                .orElseThrow(()->new IllegalStateException());
    }

}
