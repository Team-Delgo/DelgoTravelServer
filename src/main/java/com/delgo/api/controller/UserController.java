package com.delgo.api.controller;

import com.delgo.api.comm.CommController;
import com.delgo.api.comm.exception.ApiCode;
import com.delgo.api.domain.pet.Pet;
import com.delgo.api.domain.user.User;
import com.delgo.api.dto.UserDTO;
import com.delgo.api.comm.security.jwt.Access_JwtProperties;
import com.delgo.api.comm.security.jwt.Refresh_JwtProperties;
import com.delgo.api.service.PetService;
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
    private final PetService petService;
    private final TokenService tokenService;

    @PostMapping("/changePetInfo")
    public ResponseEntity<?> changePetInfo(@RequestBody UserDTO userDTO){
        try{
            String checkedEmail = userDTO.getUser().getEmail();
            if(checkedEmail == null)
                return ErrorReturn(ApiCode.PARAM_ERROR);

            User user = userService.getUserByEmail(checkedEmail);
            int userId = user.getUserId();
            Pet originPet = petService.getPetByUserId(userId);
            Pet changePet = userDTO.getPet();

            changePet.setUserId(originPet.getUserId());

            if(changePet.getBirthday() != null)
                originPet.setBirthday(changePet.getBirthday());

            if(changePet.getName() != null)
                originPet.setName(changePet.getName());

            if(changePet.getSize() != null)
                originPet.setSize(changePet.getSize());

            if(changePet.getWeight() != 0)
                originPet.setWeight(changePet.getWeight());

            petService.changePetInfo(originPet);

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
    public ResponseEntity<?> changePassword(@RequestBody UserDTO userDTO){
        try {
            String checkedEmail = userDTO.getUser().getEmail();
            String newPassword = userDTO.getUser().getPassword();
            if(checkedEmail == null || newPassword == null)
                return ErrorReturn(ApiCode.PARAM_ERROR);

            userService.changePassword(checkedEmail, newPassword);

            return SuccessReturn();

        } catch (NullPointerException e){
            return ErrorReturn(ApiCode.PARAM_ERROR);
        } catch (Exception e){
            return ErrorReturn(ApiCode.UNKNOWN_ERROR);
        }
    }

    @GetMapping("/emailAuth")
    public ResponseEntity<?> emailAuth(@RequestParam String email) {
        try {
            if(email == null)
                return ErrorReturn(ApiCode.PARAM_ERROR);

            if(userService.isEmailExisting(email)){
                return SuccessReturn(userService.getUserByEmail(email).getPhoneNo());
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
    public ResponseEntity<?> emailCheck(@RequestParam String email) {
        try {
            if(email == null)
                return ErrorReturn(ApiCode.PARAM_ERROR);

            if(userService.isEmailExisting(email) == false){
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
    public ResponseEntity<?> phoneNoAuth(@RequestParam String phoneNo) {
        try {
            if(phoneNo == null)
                return ErrorReturn(ApiCode.PARAM_ERROR);
            phoneNo = phoneNo.replaceAll("[^0-9]", "");
            if(!userService.isPhoneNoExisting(phoneNo)){
                return ErrorReturn(ApiCode.PHONE_NO_IS_NOT_EXISTING_ERROR);
            }

            int smsId = userService.sendSMS(phoneNo);
            return SuccessReturn(smsId);
        } catch (NullPointerException e){
            return ErrorReturn(ApiCode.PARAM_ERROR);
        } catch (Exception e){
            return ErrorReturn(ApiCode.UNKNOWN_ERROR);
        }
    }

    @GetMapping("/phoneNoCheck")
    public ResponseEntity<?> phoneNoCheck(@RequestParam String phoneNo) {
        try {
            if(phoneNo == null)
                return ErrorReturn(ApiCode.PARAM_ERROR);
            phoneNo = phoneNo.replaceAll("[^0-9]", "");

            if(userService.isPhoneNoExisting(phoneNo)){
                return ErrorReturn(ApiCode.PHONE_NO_IS_EXISTING_ERROR);
            }

            int smsId = userService.sendSMS(phoneNo);
            return SuccessReturn(smsId);
        } catch (NullPointerException e){
            return ErrorReturn(ApiCode.PARAM_ERROR);
        } catch (Exception e){
            return ErrorReturn(ApiCode.UNKNOWN_ERROR);
        }
    }

    @GetMapping("/authRandNum")
    public ResponseEntity<?> randNumCheck(@RequestParam int smsId, @RequestParam String enterNum) {
        try {
            if(enterNum == null)
                return ErrorReturn(ApiCode.PARAM_ERROR);

            userService.checkSMS(smsId, enterNum);
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
    public ResponseEntity<?> registerUserBySocial(@RequestBody UserDTO userDTO, HttpServletResponse response) {
        try {
            String phoneNoUpdate = userDTO.getUser().getPhoneNo().replaceAll("[^0-9]", "");
            String birthdayUpdate = userDTO.getPet().getBirthday().replaceAll("[^0-9]", "");
            userDTO.getUser().setPhoneNo(phoneNoUpdate);
            userDTO.getPet().setBirthday(birthdayUpdate);
            userDTO.getUser().setEmail("");

            User user = userService.socialSignup(userDTO.getUser(), userDTO.getPet());

            String Access_jwtToken = tokenService.createToken("Access", userDTO.getUser().getPhoneNo()); // Access Token 생성
            String Refresh_jwtToken = tokenService.createToken("Refresh", userDTO.getUser().getPhoneNo()); // Refresh Token 생성

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
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO, HttpServletResponse response) {
        try {
            String phoneNoUpdate = userDTO.getUser().getPhoneNo().replaceAll("[^0-9]", "");
            String birthdayUpdate = userDTO.getPet().getBirthday().replaceAll("[^0-9]", "");
            userDTO.getUser().setPhoneNo(phoneNoUpdate);
            userDTO.getPet().setBirthday(birthdayUpdate);
             User user = userService.signup(userDTO.getUser(), userDTO.getPet());
             user.setPassword(""); // 보안

            String Access_jwtToken = tokenService.createToken("Access", userDTO.getUser().getEmail()); // Access Token 생성
            String Refresh_jwtToken = tokenService.createToken("Refresh", userDTO.getUser().getEmail()); // Refresh Token 생성

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
    public ResponseEntity<?> deleteUser(@RequestBody UserDTO userDTO){
        try{
            String checkedEmail = userDTO.getUser().getEmail();
            if(checkedEmail == null)
                return ErrorReturn(ApiCode.PARAM_ERROR);

            userService.deleteUser(userDTO.getUser().getEmail());

            return SuccessReturn();
        } catch (NullPointerException e){
            return ErrorReturn(ApiCode.PARAM_ERROR);
        } catch (Exception e){
            return ErrorReturn(ApiCode.UNKNOWN_ERROR);
        }
    }

}
