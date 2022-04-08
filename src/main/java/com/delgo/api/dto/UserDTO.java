package com.delgo.api.dto;

import com.delgo.api.domain.pet.Pet;
import com.delgo.api.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private User user;
    private Pet pet;
}
