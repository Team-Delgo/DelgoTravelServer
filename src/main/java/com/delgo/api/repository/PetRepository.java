package com.delgo.api.repository;

import com.delgo.api.domain.pet.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PetRepository extends JpaRepository<Pet, Integer> {
    Optional<Pet> findByUserId(int userId);
}
