package com.delgo.api.controller;

import com.delgo.api.dto.common.ResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    @PostMapping("/reqBilling")
    public void reqeustBilling() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.tosspayments.com/v1/billing/authorizations/card"))
                .header("Authorization", "Basic dGVzdF9za19BRHBleE1na1czNkdKcUtFSm9CVkdiUjVvek8wOg==")
                .header("Content-Type", "application/json")
                .method("POST", HttpRequest.BodyPublishers.ofString("{\"cardNumber\":\"4330123412341234\",\"cardExpirationYear\":\"24\",\"cardExpirationMonth\":\"07\",\"cardPassword\":\"12\",\"customerIdentityNumber\":\"881212\",\"customerKey\":\"Ok-FOrvmE0_9_sSH3oGq0\"}"))
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
        String jsonString = response.body();
        JSONObject jObject = new JSONObject(jsonString);
        String billingKey = jObject.getString("billingKey");
        requestPayment(billingKey);
    }

    public ResponseEntity<?> requestPayment(String billingKey) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.tosspayments.com/v1/billing/" + billingKey))
                .header("Authorization", "Basic dGVzdF9za19BRHBleE1na1czNkdKcUtFSm9CVkdiUjVvek8wOg==")
                .header("Content-Type", "application/json")
                .method("POST", HttpRequest.BodyPublishers.ofString("{\"customerKey\":\"Ok-FOrvmE0_9_sSH3oGq0\",\"amount\":15000,\"orderId\":\"l3bmuH2rQIKEuLUVK-WuR\",\"orderName\":\"토스 티셔츠 외 2건\"}"))
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
        return ResponseEntity.ok().body(ResponseDTO.builder().code(200).codeMsg("paying is accepted").data(response.body()).build());
    }
}
