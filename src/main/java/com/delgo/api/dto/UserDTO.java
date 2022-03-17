package com.delgo.api.dto;

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
    private int user_id;
    private String name;
    private String email;
    private String password;
    private int age;
    private String phone_no;

    public User toEntity(){
        return User.builder()
                .name(name)
                .email(email)
                .password(password)
                .age(age)
                .phone_no(phone_no)
                .build();
    }

}
