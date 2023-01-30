package com.delgo.api.controller;

import com.delgo.api.comm.CommController;
import com.delgo.api.domain.pet.Pet;
import com.delgo.api.domain.user.User;
import com.delgo.api.dto.user.InfoDTO;
import com.delgo.api.dto.user.ModifyPetDTO;
import com.delgo.api.dto.user.ResetPasswordDTO;
import com.delgo.api.service.PetService;
import com.delgo.api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController extends CommController {
    private final UserService userService;
    private final PetService petService;

    @GetMapping
    public ResponseEntity<?> myAccount(@RequestParam Integer userId) {
        InfoDTO infoDTO = userService.getInfoByUserId(userId);

        return SuccessReturn(infoDTO);
    }

    // 펫 정보 수정
    @PutMapping("/pet")
    public ResponseEntity<?> changePetInfo(@Validated @RequestBody ModifyPetDTO modifyPetDTO) {
        String checkedEmail = modifyPetDTO.getEmail();

        User user = userService.getUserByEmail(checkedEmail);
        int userId = user.getUserId();
        Pet originPet = petService.getPetByUserId(userId);

        if (modifyPetDTO.getName() != null)
            originPet.setName(modifyPetDTO.getName());

        if (modifyPetDTO.getSize() != null)
            originPet.setSize(modifyPetDTO.getSize());

        petService.changePetInfo(originPet);

        return SuccessReturn();
    }

    // 비밀번호 변경 - Account Page
    @PutMapping("/password")
    public ResponseEntity<?> changePassword(@Validated @RequestBody ResetPasswordDTO resetPassword) {
        // 사용자 확인 - 토큰 사용
        userService.changePassword(resetPassword.getEmail(), resetPassword.getNewPassword());
        return SuccessReturn();
    }

    // 회원탈퇴
    @DeleteMapping(value = {"/user/{userId}", "/user"})
    public ResponseEntity<?> deleteUser(@PathVariable(value = "userId") Integer userId) {
        userService.deleteUser(userId);
        return SuccessReturn();
    }
}
