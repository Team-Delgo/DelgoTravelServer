package com.delgo.api.controller;

import com.delgo.api.comm.CommController;
import com.delgo.api.comm.exception.ApiCode;
import com.delgo.api.service.BookingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PaymentController extends CommController {

    private final BookingService bookingService;

    @PostMapping("/payment/cancel")
    public ResponseEntity<?> requestRefund(@RequestParam String paymentKey){
        HttpResponse<String> response = bookingService.requestPaymentCancel(paymentKey);

        System.out.println(response.statusCode());
        System.out.println(response.body());

        if(response.statusCode() == HttpStatus.OK.value()){
            return SuccessReturn();
        }
        return ErrorReturn(ApiCode.UNKNOWN_ERROR);


//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString((SECRET_KEY + ":").getBytes()));
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        Map<String, String> payloadMap = new HashMap<>();
//        payloadMap.put("cancelReason", "고객이 취소를 원함");
//
//        HttpEntity<String> request = null;
//        try {
//            request = new HttpEntity<>(objectMapper.writeValueAsString(payloadMap), headers);
//        } catch (JsonProcessingException e) {
//            return ErrorReturn(ApiCode.UNKNOWN_ERROR);
//        }
//
//        ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity(
//                "https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel", request, JsonNode.class);
//
//        if (responseEntity.getStatusCode() == HttpStatus.OK) {
//            return SuccessReturn();
//        } else {
//            System.out.println(responseEntity.getStatusCode());
//            return ErrorReturn(ApiCode.UNKNOWN_ERROR);
//        }



//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create("https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel"))
//                .header("Authorization", "Basic dGVzdF9za19PeUwwcVo0RzFWT0xvYkI2S3d2cm9XYjJNUVlnOg==")
//                .header("Content-Type", "application/json")
//                .method("POST", HttpRequest.BodyPublishers.ofString("{\"cancelReason\":\"고객이 취소를 원함\"}"))
//                .build();
//        HttpResponse<String> response = null;
//        try {
//            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        System.out.println(response.body());



//        if (response.statusCode() == 200) {
//            return SuccessReturn();
//        } else {
//            System.out.println(paymentKey);
//
//            System.out.println(response.statusCode());
//            return ErrorReturn(ApiCode.UNKNOWN_ERROR);
//        }

    }

}