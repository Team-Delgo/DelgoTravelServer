package com.delgo.api.controller;

import com.delgo.api.domain.user.User;
import com.delgo.api.dto.ResponseDTO;
import com.delgo.api.dto.UserDTO;
import com.delgo.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/auth")
@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO){
        try{
            User user = User.builder()
                    .name(userDTO.getName())
                    .email(userDTO.getEmail())
                    .password(userDTO.getPassword())
                    .age(userDTO.getAge())
                    .phone_no(userDTO.getPhone_no())
                    .build();
            User registeredUser = userService.create(user);
            UserDTO responseUserDTO = UserDTO.builder()
                    .user_id(registeredUser.getUser_id())
                    .name(registeredUser.getName())
                    .email(registeredUser.getEmail())
                    .password(registeredUser.getPassword())
                    .age(registeredUser.getAge())
                    .phone_no(registeredUser.getPhone_no())
                    .build();
            return ResponseEntity.ok().body(responseUserDTO);
        } catch (Exception e){
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

}
