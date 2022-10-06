package com.delgo.api.dto.user;

import com.delgo.api.domain.pet.PetSize;
import com.delgo.api.domain.user.UserSocial;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuthSignUpDTO {
    private String email;
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

    // KAKAO, NAVER 때는 필요없어서 @NotNull 넣지 않음.
    private String appleUniqueNo;
}
