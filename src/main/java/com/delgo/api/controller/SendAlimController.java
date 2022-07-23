package com.delgo.api.controller;

import com.delgo.api.comm.CommController;
import com.delgo.api.comm.exception.ApiCode;
import com.delgo.api.domain.pet.PetSize;
import com.delgo.api.service.AlimService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SendAlimController extends CommController {

    private final AlimService alimService;

    // 예약 완료 알림톡 전송
    @GetMapping("/completeReservation")
    public ResponseEntity<?> completeReservation(@RequestParam String templateCode, String phoneNo, String placeName, String roomName, PetSize petSize, String option, String startDt, String endDt) {
        try {
            if(phoneNo.isBlank() || templateCode.isBlank()){
                return ErrorReturn(ApiCode.PARAM_ERROR);
            }
            phoneNo = phoneNo.replaceAll("[^0-9]", "");

            alimService.sendWaitAlimTalk("Wait", phoneNo, placeName, roomName, petSize, option, startDt, endDt);

            return SuccessReturn();

        } catch (Exception e){
            return ErrorReturn(ApiCode.UNKNOWN_ERROR);
        }
    }
    @GetMapping("/fixReservation")
    public ResponseEntity<?> fixReservation(@RequestParam String templateCode, String phoneNo, String userName, String placeName, String roomName, PetSize petSize, String option, String startDt, String endDt) {
        try {
            if(phoneNo.isBlank() || templateCode.isBlank()){
                return ErrorReturn(ApiCode.PARAM_ERROR);
            }
            phoneNo = phoneNo.replaceAll("[^0-9]", "");

            alimService.sendFixAlimTalk("Fix", phoneNo, userName, placeName, roomName, petSize, option, startDt, endDt);

            return SuccessReturn();

        } catch (Exception e){
            return ErrorReturn(ApiCode.UNKNOWN_ERROR);
        }
    }
    @GetMapping("/cancelReservation")
    public ResponseEntity<?> cancelReservation(@RequestParam String templateCode, String phoneNo, String placeName, String roomName, String price, String payment) {
        try {
            if(phoneNo.isBlank() || templateCode.isBlank()){
                return ErrorReturn(ApiCode.PARAM_ERROR);
            }
            phoneNo = phoneNo.replaceAll("[^0-9]", "");

            alimService.sendCancelAlimTalk("Cancel", phoneNo, placeName, roomName, price, payment);

            return SuccessReturn();

        } catch (Exception e){
            return ErrorReturn(ApiCode.UNKNOWN_ERROR);
        }
    }
}
