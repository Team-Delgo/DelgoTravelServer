package com.delgo.api.controller;

import com.delgo.api.comm.CommController;
import com.delgo.api.comm.exception.ApiCode;
import com.delgo.api.domain.user.User;
import com.delgo.api.dto.UserDTO;
import com.delgo.api.comm.security.jwt.Access_JwtProperties;
import com.delgo.api.comm.security.jwt.Refresh_JwtProperties;
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
public class UserController extends CommController {

    private final UserService userService;
    private final TokenService tokenService;

    @PostMapping("/changePetInfo")
    public ResponseEntity<?> changePetInfo(@RequestBody Optional<UserDTO> userDTO){
        try{



            return SuccessReturn();
        } catch (IllegalStateException e){
            return ErrorReturn(ApiCode.NOT_FOUND_DATA);
        } catch (NullPointerException e){
            return ErrorReturn(ApiCode.PARAM_ERROR);
        } catch (Exception e){
            return ErrorReturn(ApiCode.UNKNOWN_ERROR);
        }
    }

    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestBody Optional<UserDTO> userDTO){
        try {
            UserDTO checkedUserDTO = userDTO.orElseThrow(() -> new NullPointerException("Param Empty"));
            String checkedEmail = checkedUserDTO.getUser().getEmail();
            String newPassword = checkedUserDTO.getUser().getPassword();
            if(checkedEmail == null || newPassword == null)
                throw new NullPointerException("Param Empty");

            userService.changePassword(checkedEmail, newPassword);

            return SuccessReturn();

        } catch (NullPointerException e){
            return ErrorReturn(ApiCode.PARAM_ERROR);
        } catch (Exception e){
            return ErrorReturn(ApiCode.UNKNOWN_ERROR);
        }
    }

    @GetMapping("/emailAuth")
    public ResponseEntity<?> emailAuth(Optional<String> email) {
        try {
            String checkedEmail = email.orElseThrow(() -> new NullPointerException("Param Empty"));

            if(userService.isEmailExisting((checkedEmail))){
                return SuccessReturn(userService.findByEmail(checkedEmail).getPhoneNo());
            } else {
                return ErrorReturn(ApiCode.EMAIL_IS_NOT_EXISTING_ERROR);
            }
        } catch (NullPointerException e){
            return ErrorReturn(ApiCode.PARAM_ERROR);
        } catch (Exception e){
            return ErrorReturn(ApiCode.UNKNOWN_ERROR);
        }
    }

    @GetMapping("/emailCheck")
    public ResponseEntity<?> emailCheck(Optional<String> email) {
        try { // Param Empty Check
            String checkedEmail = email.orElseThrow(() -> new NullPointerException("Param Empty"));

            if(userService.isEmailExisting(checkedEmail) == false){
                return SuccessReturn();
            } else {
                return ErrorReturn(ApiCode.EMAIL_IS_EXISTING_ERROR);
            }
        } catch (NullPointerException e){
            return ErrorReturn(ApiCode.PARAM_ERROR);
        } catch (Exception e){
            return ErrorReturn(ApiCode.UNKNOWN_ERROR);
        }
    }

    @GetMapping("/phoneNoAuth")
    public ResponseEntity<?> phoneNoAuth(Optional<String> phoneNo) {
        try {
            String checkedPhoneNo = phoneNo.orElseThrow(() -> new NullPointerException("Param Empty"));
            checkedPhoneNo = checkedPhoneNo.replaceAll("[^0-9]", "");
            if(!userService.isPhoneNoExisting(checkedPhoneNo)){
                return ErrorReturn(ApiCode.PHONE_NO_IS_NOT_EXISTING_ERROR);
            }

            int smsId = userService.sendSMS(checkedPhoneNo);
            return SuccessReturn(smsId);
        } catch (NullPointerException e){
            return ErrorReturn(ApiCode.PARAM_ERROR);
        } catch (Exception e){
            return ErrorReturn(ApiCode.UNKNOWN_ERROR);
        }
    }

    @GetMapping("/phoneNoCheck")
    public ResponseEntity<?> phoneNoCheck(Optional<String> phoneNo) {
        try {
            String checkedPhoneNo = phoneNo.orElseThrow(() -> new NullPointerException("Param Empty"));
            checkedPhoneNo = checkedPhoneNo.replaceAll("[^0-9]", "");
            if(userService.isPhoneNoExisting(checkedPhoneNo)){
                return ErrorReturn(ApiCode.PHONE_NO_IS_EXISTING_ERROR);
            }

            int smsId = userService.sendSMS(checkedPhoneNo);
            return SuccessReturn(smsId);
        } catch (NullPointerException e){
            return ErrorReturn(ApiCode.PARAM_ERROR);
        } catch (Exception e){
            return ErrorReturn(ApiCode.UNKNOWN_ERROR);
        }
    }

    @GetMapping("/authRandNum")
    public ResponseEntity<?> randNumCheck(int smsId, Optional<String> enterNum) {
        try {
            String checkedEnterNum = enterNum.orElseThrow(() -> new NullPointerException("Param Empty"));
            userService.checkSMS(smsId, checkedEnterNum);
            return SuccessReturn();
        } catch (IllegalStateException e){
            return ErrorReturn(ApiCode.AUTH_NO_IS_NOT_MATCHING);
        } catch (NullPointerException e){
            return ErrorReturn(ApiCode.PARAM_ERROR);
        } catch (Exception e){
            return ErrorReturn(ApiCode.UNKNOWN_ERROR);
        }
    }

    @PostMapping("/socialSignup")
    public ResponseEntity<?> registerUserBySocial(@RequestBody Optional<UserDTO> userDTO, HttpServletResponse response) {
        try { // Param Empty Check
            UserDTO checkedUserDTO = userDTO.orElseThrow(() -> new NullPointerException("Param Empty"));
            String phoneNoUpdate = checkedUserDTO.getUser().getPhoneNo().replaceAll("[^0-9]", "");
            checkedUserDTO.getUser().setPhoneNo(phoneNoUpdate);
            checkedUserDTO.getUser().setEmail("");

            User user = userService.socialSignup(checkedUserDTO.getUser(), checkedUserDTO.getPet());

            String Access_jwtToken = tokenService.createToken("Access", checkedUserDTO.getUser().getPhoneNo()); // Access Token 생성
            String Refresh_jwtToken = tokenService.createToken("Refresh", checkedUserDTO.getUser().getPhoneNo()); // Refresh Token 생성

            response.addHeader(Access_JwtProperties.HEADER_STRING, Access_JwtProperties.TOKEN_PREFIX + Access_jwtToken);
            response.addHeader(Refresh_JwtProperties.HEADER_STRING, Refresh_JwtProperties.TOKEN_PREFIX + Refresh_jwtToken);

            return SuccessReturn(user);
        } catch (NullPointerException e){
            return ErrorReturn(ApiCode.PARAM_ERROR);
        } catch (Exception e){
            return ErrorReturn(ApiCode.UNKNOWN_ERROR);
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

            return SuccessReturn(user);
        } catch (NullPointerException e){
            return ErrorReturn(ApiCode.PARAM_ERROR);
        } catch (Exception e){
            return ErrorReturn(ApiCode.UNKNOWN_ERROR);
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

            return SuccessReturn();
        } catch (NullPointerException e){
            return ErrorReturn(ApiCode.PARAM_ERROR);
        } catch (Exception e){
            return ErrorReturn(ApiCode.UNKNOWN_ERROR);
        }
    }

}
