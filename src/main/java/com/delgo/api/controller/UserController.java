package com.delgo.api.controller;

import com.delgo.api.domain.user.User;
import com.delgo.api.dto.UserDTO;
import com.delgo.api.dto.common.ResponseDTO;
import com.delgo.api.config.security.jwt.Access_JwtProperties;
import com.delgo.api.config.security.jwt.Refresh_JwtProperties;
import com.delgo.api.service.TokenService;
import com.delgo.api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final TokenService tokenService;

    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestBody Optional<UserDTO> userDTO){
        try {
            UserDTO checkedUserDTO = userDTO.orElseThrow(() -> new NullPointerException("Param Empty"));
            String checkedEmail = checkedUserDTO.getUser().getEmail();
            String newPassword = checkedUserDTO.getUser().getPassword();
            if(checkedEmail == null || newPassword == null)
                throw new NullPointerException("Param Empty");

            userService.changePassword(checkedEmail, newPassword);

            return ResponseEntity.ok().body(ResponseDTO.builder().code(200).codeMsg("Change password success").build());

        } catch (Exception e){
            return ResponseEntity.badRequest().body(ResponseDTO.builder().code(303).codeMsg(e.getMessage()).build()
            );
        }
    }

    @GetMapping("/emailAuth")
    public ResponseEntity<?> emailAuth(Optional<String> email) {
        try {
            String checkedEmail = email.orElseThrow(() -> new NullPointerException("Param Empty"));

            return (userService.isEmailExisting(checkedEmail)) ?
                    ResponseEntity.ok().body(ResponseDTO.builder().code(200).codeMsg("The email exists").data(userService.findByEmail(checkedEmail).getPhoneNo()).build()) :
                    ResponseEntity.ok().body(ResponseDTO.builder().code(303).codeMsg("The email does not exist").build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDTO.builder().code(303).codeMsg(e.getMessage()).build()
            );
        }
    }

    @GetMapping("/emailCheck")
    public ResponseEntity<?> emailCheck(Optional<String> email) {
        try { // Param Empty Check
            String checkedEmail = email.orElseThrow(() -> new NullPointerException("Param Empty"));

            return (userService.isEmailExisting(checkedEmail)) ?
                    ResponseEntity.ok().body(ResponseDTO.builder().code(303).codeMsg("Email is duplicate").build()) : // 이메일 중복 0
                    ResponseEntity.ok().body(ResponseDTO.builder().code(200).codeMsg("No duplicate").build()); // 이메일 중복 x
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDTO.builder().code(303).codeMsg(e.getMessage()).build()
            );
        }
    }

    String randNum;

    @GetMapping("/phoneNoCheck")
    public ResponseEntity<?> phoneNoCheck(Optional<String> phoneNo) {
        try {
            String checkedPhoneNo = phoneNo.orElseThrow(() -> new NullPointerException("Param Empty"));
            checkedPhoneNo = checkedPhoneNo.replaceAll("[^0-9]", "");
            int smsId = userService.sendSMS(checkedPhoneNo);
            return ResponseEntity.ok().body(
                    ResponseDTO.builder().code(200).codeMsg("sending phone number check sms success").data("smsId: " + smsId).build()
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDTO.builder().code(303).codeMsg("sending phone number check sms failed").build());
        }
    }

    @GetMapping("/authRandNum")
    public ResponseEntity<?> randNumCheck(int smsId, Optional<String> enterNum) {
        try {
            String checkedEnterNum = enterNum.orElseThrow(() -> new NullPointerException("Param Empty"));
            userService.checkSMS(smsId, checkedEnterNum);
            return ResponseEntity.ok().body(
                    ResponseDTO.builder().code(200).codeMsg("PhoneNo is authorized").build()
            );
        }
        catch (IllegalStateException e){
            return ResponseEntity.ok().body(ResponseDTO.builder().code(303).codeMsg(e.getMessage()).build());
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ResponseDTO.builder().code(303).codeMsg(e.getMessage()).build()
            );
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody Optional<UserDTO> userDTO, HttpServletResponse response) {
        try { // Param Empty Check
            UserDTO checkedUserDTO = userDTO.orElseThrow(() -> new NullPointerException("Param Empty"));
            String phoneNoUpdate = checkedUserDTO.getUser().getPhoneNo().replaceAll("[^0-9]", "");
            checkedUserDTO.getUser().setPhoneNo(phoneNoUpdate);
             User user = userService.signup(checkedUserDTO.getUser(), checkedUserDTO.getPet());
             user.setPassword(""); // 보안

            String Access_jwtToken = tokenService.createToken("Access", checkedUserDTO.getUser().getEmail()); // Access Token 생성
            String Refresh_jwtToken = tokenService.createToken("Refresh", checkedUserDTO.getUser().getEmail()); // Refresh Token 생성

            response.addHeader(Access_JwtProperties.HEADER_STRING, Access_JwtProperties.TOKEN_PREFIX + Access_jwtToken);
            response.addHeader(Refresh_JwtProperties.HEADER_STRING, Refresh_JwtProperties.TOKEN_PREFIX + Refresh_jwtToken);

            return ResponseEntity.ok().body(ResponseDTO.builder().code(200).codeMsg("signup success").data(user).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ResponseDTO.builder().code(303).codeMsg(e.getMessage()).build());
        }
    }

    @PostMapping("/deleteUser")
    public ResponseEntity<?> deleteUser(@RequestBody Optional<UserDTO> userDTO){
        try{
            UserDTO checkedUserDTO = userDTO.orElseThrow(() -> new NullPointerException("Param Empty"));
            String checkedEmail = checkedUserDTO.getUser().getEmail();
            if(checkedEmail == null)
                throw new NullPointerException("Param Empty");

            userService.deleteUser(checkedUserDTO.getUser().getEmail());

            return ResponseEntity.ok().body(ResponseDTO.builder().code(200).codeMsg("delete user success").build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ResponseDTO.builder().code(303).codeMsg(e.getMessage()).build());
        }
    }

}
