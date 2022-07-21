package com.delgo.api.controller;

import com.delgo.api.comm.CommController;
import com.delgo.api.comm.exception.ApiCode;
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
    public ResponseEntity<?> completeReservation(@RequestParam String templateCode, String phoneNo) {
        try {
            if(phoneNo.isBlank() || templateCode.isBlank()){
                return ErrorReturn(ApiCode.PARAM_ERROR);
            }
            phoneNo = phoneNo.replaceAll("[^0-9]", "");

            alimService.sendAlimTalk(templateCode, phoneNo);

            return SuccessReturn();

        } catch (Exception e){
            return ErrorReturn(ApiCode.UNKNOWN_ERROR);
        }
    }
}
