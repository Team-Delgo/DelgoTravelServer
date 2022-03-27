package com.delgo.api.dto;

import com.delgo.api.domain.pet.Pet;
import com.delgo.api.domain.pet.PetSize;
import com.delgo.api.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetDTO {
    private int pet_id;
    private String name;
    private int age;
    private String breed;
    private PetSize size;
    private String birthday;
    private User user;

    public Pet toEntity(){
        return Pet.builder()
                .pet_id(pet_id)
                .name(name)
                .age(age)
                .breed(breed)
                .size(size)
                .birthday(birthday)
                .user(user)
                .build();
    }
}
