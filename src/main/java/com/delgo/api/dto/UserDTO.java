package com.delgo.api.dto;

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

}
