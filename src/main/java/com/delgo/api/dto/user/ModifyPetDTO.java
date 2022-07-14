package com.delgo.api.dto.user;

import com.delgo.api.domain.pet.PetSize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
public class ModifyPetDTO {
    @NotNull
    private String email;
    private String name;
    private String birthday;
    private PetSize size;
}
