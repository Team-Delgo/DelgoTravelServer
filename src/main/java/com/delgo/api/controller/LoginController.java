package com.delgo.api.controller;

import com.delgo.api.domain.user.User;
import com.delgo.api.dto.common.ResponseDTO;
import com.delgo.api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;

    @PostMapping("/loginSuccess")
    public ResponseEntity<?> loginSuccess(HttpServletRequest request, HttpServletResponse response) {
        String email = request.getAttribute("email").toString();
        User user = userService.findByEmail(email);

        ResponseDTO responseDTO = ResponseDTO.builder().code(200).codeMsg("success").data(user).build();
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/loginFail")
    public ResponseEntity<?> loginFail() {
        log.info("LoginController: Fail");
        ResponseDTO responseDTO = ResponseDTO.builder().code(407).codeMsg("fail").build(); // code는 논의 필요
        return ResponseEntity.ok().body(responseDTO);
    }
}
