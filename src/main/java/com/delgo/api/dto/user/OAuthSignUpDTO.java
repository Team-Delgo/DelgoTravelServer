package com.delgo.api.dto.user;

import com.delgo.api.domain.pet.Pet;
import com.delgo.api.domain.pet.PetSize;
import com.delgo.api.domain.user.User;
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

    public User makeUserSocial(UserSocial userSocial){
        return User.builder()
                .name(userName)
                .email(email)
                .phoneNo(phoneNo.replaceAll("[^0-9]", ""))
                .userSocial(userSocial)
                .build();
    }

    public User makeUserApple(String appleUniqueNo){
        return User.builder()
                .name(userName)
                .email(email)
                .phoneNo(phoneNo.replaceAll("[^0-9]", ""))
                .userSocial(UserSocial.A)
                .appleUniqueNo(appleUniqueNo)
                .build();
    }

    public Pet makePet(){
        return Pet.builder()
                .name(petName)
                .size(petSize)
                .birthday(birthday)
                .build();
    }

}
