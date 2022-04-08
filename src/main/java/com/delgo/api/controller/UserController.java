package com.delgo.api.controller;

import com.delgo.api.dto.UserDTO;
import com.delgo.api.dto.common.ResponseDTO;
import com.delgo.api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/emailCheck")
    public ResponseEntity<?> emailCheck(Optional<String> email) {
        try {
            // Param Empty Check
            String checkedEmail = email.orElseThrow(() -> new NullPointerException("Param Empty"));

            userService.validateDuplicate(checkedEmail);
            return ResponseEntity.ok().body(
                    ResponseDTO.builder().code(200).codeMsg("No duplicate").build()
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ResponseDTO.builder().code(303).codeMsg(e.getMessage()).build()
            );
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody Optional<UserDTO> userDTO) {
        try {
            // Param Empty Check
            UserDTO checkedUserDTO = userDTO.orElseThrow(() -> new NullPointerException("Param Empty"));

            userService.create(checkedUserDTO.getUser(), checkedUserDTO.getPet());
            return ResponseEntity.ok().body(
                    ResponseDTO.builder().code(200).codeMsg("signup success").build()
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ResponseDTO.builder().code(303).codeMsg(e.getMessage()).build()
            );
        }
    }
}
