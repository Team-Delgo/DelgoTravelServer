package com.delgo.api.controller;

import com.delgo.api.comm.CommController;
import com.delgo.api.comm.exception.ApiCode;
import com.delgo.api.service.SendLmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SendLmsController extends CommController {

    private final SendLmsService sendLmsService;
//
//    // 예약 완료 발송
//    @GetMapping("/completeReservation")
//    public ResponseEntity<?> completeReservation(@RequestParam String phoneNo) {
//        try {
//            if(phoneNo.isBlank()){
//                return ErrorReturn(ApiCode.PARAM_ERROR);
//            }
//            phoneNo = phoneNo.replaceAll("[^0-9]", "");
//            String subject = "[Delgo] 예약 완료 안내";
//            String message = "내용";
//            sendLmsService.sendLms(phoneNo, subject, message);
//
//            return SuccessReturn();
//
//        } catch (Exception e){
//            return ErrorReturn(ApiCode.UNKNOWN_ERROR);
//        }
//    }
//    // 예약 확정 발송
//    @GetMapping("/confirmReservation")
//    public ResponseEntity<?> confirmReservation(@RequestParam String phoneNo) {
//        try {
//            if(phoneNo.isBlank()){
//                return ErrorReturn(ApiCode.PARAM_ERROR);
//            }
//            phoneNo = phoneNo.replaceAll("[^0-9]", "");
//
//            String subject = "[Delgo] 예약 확정 안내";
//            String message = "내용";
//            sendLmsService.sendLms(phoneNo, subject, message);
//
//            return SuccessReturn();
//
//        } catch (Exception e){
//            return ErrorReturn(ApiCode.UNKNOWN_ERROR);
//        }
//    }
//    // 예약 취소 발송
//    @GetMapping("/cancelReservation")
//    public ResponseEntity<?> cancelReservation(@RequestParam String phoneNo) {
//        try {
//            if(phoneNo.isBlank()){
//                return ErrorReturn(ApiCode.PARAM_ERROR);
//            }
//            phoneNo = phoneNo.replaceAll("[^0-9]", "");
//
//            String subject = "[Delgo] 예약 취소 안내";
//            String message = "내용";
//            sendLmsService.sendLms(phoneNo, subject, message);
//
//            return SuccessReturn();
//
//        } catch (Exception e){
//            return ErrorReturn(ApiCode.UNKNOWN_ERROR);
//        }
//    }
}
