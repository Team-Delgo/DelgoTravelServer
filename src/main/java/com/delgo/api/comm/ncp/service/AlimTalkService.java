package com.delgo.api.comm.ncp.service;

import com.delgo.api.comm.ncp.dto.alimTalk.AlimTalkButtonDTO;
import com.delgo.api.comm.ncp.dto.alimTalk.AlimTalkMessageDTO;
import com.delgo.api.comm.ncp.dto.alimTalk.AlimTalkRequestDTO;
import com.delgo.api.comm.ncp.dto.alimTalk.AlimTalkResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Component
public class AlimTalkService {
    // 플친 아이디
    String plusFriendId ="@delgo";

    String requestUrlHeader = "https://sens.apigw.ntruss.com";    	// 요청 URL
    String requestUrlService = "/alimtalk/v2/services/";
    String requestUrlType = "/messages";                      		// 요청 URL
    String accessKey = "CU54eUVGT4dRhR7H1ocm";                     						// 네이버 클라우드 플랫폼 회원에게 발급되는 개인 인증키
    String sigSecretKey = "oCzPFBWmPMFYCf6Z9FU6iMMBtXB1RR7UdGV2BZuS";
    String serviceId = "ncp:kkobizmsg:kr:2717885:delgo-kakaotalk";        									// 프로젝트에 할당된 SMS 서비스 ID
    String method = "POST";

    String apiUrl = requestUrlHeader + requestUrlService + serviceId + requestUrlType;
    String sigUrl = requestUrlService + serviceId + requestUrlType;

    public AlimTalkResponseDTO sendAlimTalk(String templateCode, String recipientPhoneNumber, String content) throws JsonProcessingException, UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException {
        long time = System.currentTimeMillis();
        String timeStamp = Long.toString(time);
        List<AlimTalkMessageDTO> messages = new ArrayList<>();


        AlimTalkButtonDTO alimTalkButtonDTO = new AlimTalkButtonDTO("WL", "Delgo에서 보기", "http://www.delgo.pet/", "http://www.delgo.pet/");
        List<AlimTalkButtonDTO> buttons = new ArrayList<>();
        buttons.add(alimTalkButtonDTO);

        messages.add(new AlimTalkMessageDTO("82", recipientPhoneNumber,  content, buttons));
        AlimTalkRequestDTO alimTalkRequestDTO = new AlimTalkRequestDTO(plusFriendId, templateCode, messages);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(alimTalkRequestDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/json; charset=utf-8"));
        headers.set("x-ncp-apigw-timestamp", timeStamp);
        headers.set("x-ncp-iam-access-key", accessKey);
        String sig = makeSignature(timeStamp);
        headers.set("x-ncp-apigw-signature-v2", sig);
        HttpEntity<String> body = new HttpEntity<>(jsonBody, headers);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
            public boolean hasError(ClientHttpResponse response) throws IOException {
                HttpStatus statusCode = response.getStatusCode();
                System.out.println("SendAlimTalk StatusCode: " + statusCode);
                if(statusCode.series() != HttpStatus.Series.SUCCESSFUL) {
                    throw new IOException();
                }
                return statusCode.series() == HttpStatus.Series.SERVER_ERROR;
            }
        });
        AlimTalkResponseDTO alimTalkResponseDTO = restTemplate.postForObject(apiUrl, body, AlimTalkResponseDTO.class);
        System.out.println("alimTalkResponseDTO: " + alimTalkResponseDTO);
        return alimTalkResponseDTO;
    }
    public String makeSignature(String time) throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException {
        String space = " ";
        String newLine = "\n";
        String message = new StringBuilder()
                .append(method)
                .append(space)
                .append(sigUrl)
                .append(newLine)
                .append(time)
                .append(newLine)
                .append(accessKey)
                .toString();
        SecretKeySpec signingKey = new SecretKeySpec(sigSecretKey.getBytes("UTF-8"), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);
        byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
        String encodeBase64String = Base64.encodeBase64String(rawHmac);
        return encodeBase64String;
    }
}
