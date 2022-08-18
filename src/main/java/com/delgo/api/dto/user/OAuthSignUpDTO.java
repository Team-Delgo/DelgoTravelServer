package com.delgo.api.dto.user;

import com.delgo.api.domain.pet.PetSize;
import com.delgo.api.domain.user.UserSocial;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class OAuthSignUpDTO {
    @NotBlank
    private String userName;
    @NotBlank
    private String phoneNo;
    @NotBlank
    private String petName;
    @NotNull
    private PetSize petSize;
    @NotNull
    private LocalDate birthday;
    @NotNull
    private UserSocial userSocial;
}
