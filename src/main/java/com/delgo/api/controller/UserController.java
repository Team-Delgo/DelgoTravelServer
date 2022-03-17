package com.delgo.api.controller;

import com.delgo.api.domain.user.User;
import com.delgo.api.dto.common.ResponseDTO;
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
            User user = userService.create(userDTO);
            ResponseDTO responseDTO = ResponseDTO.builder().code(200).codeMsg("success").build();
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e){
            ResponseDTO responseDTO = ResponseDTO.builder().code(400).codeMsg(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

}
