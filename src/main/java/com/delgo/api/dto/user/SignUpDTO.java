package com.delgo.api.dto.user;

import com.delgo.api.domain.pet.Pet;
import com.delgo.api.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
public class SignUpDTO {
    @NotNull
    private User user;
    @NotNull
    private Pet pet;
}
