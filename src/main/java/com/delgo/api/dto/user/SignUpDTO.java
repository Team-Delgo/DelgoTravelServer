package com.delgo.api.dto.user;

import com.delgo.api.domain.pet.Pet;
import com.delgo.api.domain.pet.PetSize;
import com.delgo.api.domain.user.User;
import com.delgo.api.domain.user.UserSocial;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class SignUpDTO {
    @NotBlank private String userName;
    @NotBlank private String email;
    @NotBlank private String password;
    @NotBlank private String phoneNo;
    @NotBlank private String petName;
    @NotNull private PetSize petSize;
    @NotNull private LocalDate birthday;

    public User makeUser(String password){
        return User.builder()
                .name(userName)
                .email(email)
                .password(password)
                .phoneNo(phoneNo.replaceAll("[^0-9]", ""))
                .userSocial(UserSocial.D)
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
