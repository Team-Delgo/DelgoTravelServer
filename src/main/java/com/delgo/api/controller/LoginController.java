package com.delgo.api.controller;

import com.delgo.api.dto.common.ResponseDTO;
import com.delgo.api.security.jwt.JwtProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LoginController {

    @PostMapping("/loginSuccess")
    public ResponseEntity<?> loginSuccess(HttpServletResponse response) {
        log.info("LoginController: Success header " + response.getHeader(JwtProperties.HEADER_STRING));

        ResponseDTO responseDTO = ResponseDTO.builder().code(200).codeMsg("login Success").build();
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/loginFail")
    public ResponseEntity<?> loginFail() {
        log.info("LoginController: Fail");
        ResponseDTO responseDTO = ResponseDTO.builder().code(407).codeMsg("login Fail").build(); // code는 논의 필요
        return ResponseEntity.ok().body(responseDTO);
    }
}
