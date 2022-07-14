package com.delgo.api.controller;

import com.delgo.api.comm.CommController;
import com.delgo.api.comm.exception.ApiCode;
import com.delgo.api.domain.pet.Pet;
import com.delgo.api.domain.user.User;
import com.delgo.api.dto.user.InfoDTO;
import com.delgo.api.dto.user.ModifyPetDTO;
import com.delgo.api.dto.user.SignUpDTO;
import com.delgo.api.comm.security.jwt.Access_JwtProperties;
import com.delgo.api.comm.security.jwt.Refresh_JwtProperties;
import com.delgo.api.service.PetService;
import com.delgo.api.service.TokenService;
import com.delgo.api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController extends CommController {

    private final UserService userService;
    private final PetService petService;
    private final TokenService tokenService;

    @GetMapping("/myAccount")
    public ResponseEntity<?> myAccount(@RequestParam Integer userId){
        InfoDTO infoDTO = userService.getInfoByUserId(userId);

        return SuccessReturn(infoDTO);
    }

    // 펫 정보 수정
    @PostMapping("/changePetInfo")
    public ResponseEntity<?> changePetInfo(@Validated @RequestBody ModifyPetDTO modifyPetDTO){
        String checkedEmail = modifyPetDTO.getEmail();

        User user = userService.getUserByEmail(checkedEmail);
        int userId = user.getUserId();
        Pet originPet = petService.getPetByUserId(userId);

        if(modifyPetDTO.getBirthday() != null)
            originPet.setBirthday(modifyPetDTO.getBirthday());

        if(modifyPetDTO.getName() != null)
            originPet.setName(modifyPetDTO.getName());

        if(modifyPetDTO.getSize() != null)
            originPet.setSize(modifyPetDTO.getSize());

        petService.changePetInfo(originPet);

        return SuccessReturn();
    }

    // 비밀번호 수정
    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(@Validated @RequestBody SignUpDTO signUpDTO){
        String checkedEmail = signUpDTO.getUser().getEmail();
        String newPassword = signUpDTO.getUser().getPassword();

        if(checkedEmail == null || newPassword == null)
            return ErrorReturn(ApiCode.PARAM_ERROR);

        userService.changePassword(checkedEmail, newPassword);

        return SuccessReturn();
    }

    // 이메일 존재 유무 확인
    @GetMapping("/emailAuth")
    public ResponseEntity<?> emailAuth(@RequestParam String email) {
        if(email.isBlank()){
            return ErrorReturn(ApiCode.PARAM_ERROR);
        }

        if(userService.isEmailExisting(email)) {
            return SuccessReturn(userService.getUserByEmail(email).getPhoneNo());
        }
        return ErrorReturn(ApiCode.EMAIL_NOT_EXIST);
    }

    // 이메일 중복 확인
    @GetMapping("/emailCheck")
    public ResponseEntity<?> emailCheck(@RequestParam String email) {
        if(email.isBlank()){
            return ErrorReturn(ApiCode.PARAM_ERROR);
        }
        if(!userService.isEmailExisting(email)){
            return SuccessReturn();
        } else {
            return ErrorReturn(ApiCode.EMAIL_DUPLICATE_ERROR);
        }
    }

    // 회원가입 시 인증번호 전송 -> sms 인증은 try catch로
    @GetMapping("/phoneNoAuth")
    public ResponseEntity<?> phoneNoCheck(@RequestParam String phoneNo) {
        try {
            if(phoneNo.isBlank()){
                return ErrorReturn(ApiCode.PARAM_ERROR);
            }
            phoneNo = phoneNo.replaceAll("[^0-9]", "");

            if(userService.isPhoneNoExisting(phoneNo)){
                return ErrorReturn(ApiCode.PHONE_NO_DUPLICATE_ERROR);
            } else {
                int smsId = userService.sendSMS(phoneNo);
                return SuccessReturn(smsId);
            }
        } catch (NullPointerException e){
            return ErrorReturn(ApiCode.PARAM_ERROR);
        } catch (Exception e){
            return ErrorReturn(ApiCode.UNKNOWN_ERROR);
        }
    }

    // 회원가입 후 인증번호 전송
    @GetMapping("/phoneNoCheck")
    public ResponseEntity<?> phoneNoAuth(@RequestParam String phoneNo) {
        try {
            if(phoneNo.isBlank()){
                return ErrorReturn(ApiCode.PARAM_ERROR);
            }
            phoneNo = phoneNo.replaceAll("[^0-9]", "");
            if(!userService.isPhoneNoExisting(phoneNo)){
                return ErrorReturn(ApiCode.PHONE_NO_NOT_EXIST);
            } else {
                int smsId = userService.sendSMS(phoneNo);
                return SuccessReturn(smsId);
            }
        } catch (NullPointerException e){
            return ErrorReturn(ApiCode.PARAM_ERROR);
        } catch (Exception e){
            return ErrorReturn(ApiCode.UNKNOWN_ERROR);
        }
    }

    // 인증번호 확인
    @GetMapping("/authRandNum")
    public ResponseEntity<?> randNumCheck(@RequestParam Integer smsId, @RequestParam String enterNum) {
        if(enterNum.isBlank()){
            return ErrorReturn(ApiCode.PARAM_ERROR);
        }

        userService.checkSMS(smsId, enterNum);
        return SuccessReturn();
    }

    // 소셜 회원가입
    @PostMapping("/socialSignup")
    public ResponseEntity<?> registerUserBySocial(@Validated @RequestBody SignUpDTO signUpDTO, HttpServletResponse response) {
        String phoneNoUpdate = signUpDTO.getUser().getPhoneNo().replaceAll("[^0-9]", "");
        String birthdayUpdate = signUpDTO.getPet().getBirthday().replaceAll("[^0-9]", "");
        signUpDTO.getUser().setPhoneNo(phoneNoUpdate);
        signUpDTO.getPet().setBirthday(birthdayUpdate);
        signUpDTO.getUser().setEmail("");

        User user = userService.socialSignup(signUpDTO.getUser(), signUpDTO.getPet());

        String Access_jwtToken = tokenService.createToken("Access", signUpDTO.getUser().getPhoneNo()); // Access Token 생성
        String Refresh_jwtToken = tokenService.createToken("Refresh", signUpDTO.getUser().getPhoneNo()); // Refresh Token 생성

        response.addHeader(Access_JwtProperties.HEADER_STRING, Access_JwtProperties.TOKEN_PREFIX + Access_jwtToken);
        response.addHeader(Refresh_JwtProperties.HEADER_STRING, Refresh_JwtProperties.TOKEN_PREFIX + Refresh_jwtToken);

        return SuccessReturn(user);
    }

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Validated @RequestBody SignUpDTO signUpDTO, HttpServletResponse response) {
        String phoneNoUpdate = signUpDTO.getUser().getPhoneNo().replaceAll("[^0-9]", "");
        String birthdayUpdate = signUpDTO.getPet().getBirthday().replaceAll("[^0-9]", "");
        signUpDTO.getUser().setPhoneNo(phoneNoUpdate);
        signUpDTO.getPet().setBirthday(birthdayUpdate);
        User user = userService.signup(signUpDTO.getUser(), signUpDTO.getPet());
        user.setPassword(""); // 보안

        String Access_jwtToken = tokenService.createToken("Access", signUpDTO.getUser().getEmail()); // Access Token 생성
        String Refresh_jwtToken = tokenService.createToken("Refresh", signUpDTO.getUser().getEmail()); // Refresh Token 생성

        response.addHeader(Access_JwtProperties.HEADER_STRING, Access_JwtProperties.TOKEN_PREFIX + Access_jwtToken);
        response.addHeader(Refresh_JwtProperties.HEADER_STRING, Refresh_JwtProperties.TOKEN_PREFIX + Refresh_jwtToken);

        return SuccessReturn(user);
    }

    // 회원탈퇴
    @PostMapping("/deleteUser")
    public ResponseEntity<?> deleteUser(@Validated @RequestBody SignUpDTO signUpDTO){

        userService.deleteUser(signUpDTO.getUser().getEmail());
        return SuccessReturn();
    }

}
