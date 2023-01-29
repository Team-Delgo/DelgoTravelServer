package com.delgo.api.controller;

import com.delgo.api.comm.CommController;
import com.delgo.api.comm.exception.ApiCode;
import com.delgo.api.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.net.http.HttpResponse;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController extends CommController {

    private final BookingService bookingService;

    @PostMapping("/cancel")
    public ResponseEntity<?> requestRefund(@RequestParam String paymentKey){
        HttpResponse<String> response = bookingService.requestPaymentCancel(paymentKey);

        System.out.println(response.statusCode());
        System.out.println(response.body());

        if(response.statusCode() == HttpStatus.OK.value()){
            return SuccessReturn();
        }
        return ErrorReturn(ApiCode.UNKNOWN_ERROR);

    }

}