package com.delgo.api.dto;

import lombok.*;

import java.sql.Timestamp;


@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SendSmsResponseDTO {
    private String statusCode;
    private String statusName;
    private String requestId;
    private Timestamp requestTime;

}