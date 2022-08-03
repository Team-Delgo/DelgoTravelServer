package com.delgo.api.dto.user;

import com.delgo.api.domain.pet.Pet;
import com.delgo.api.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserPetDTO {
    private User user;
    private Pet pet;
}
