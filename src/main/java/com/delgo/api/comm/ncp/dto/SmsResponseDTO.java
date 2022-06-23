package com.delgo.api.comm.ncp.dto;

import lombok.*;

import java.sql.Timestamp;


@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SmsResponseDTO {
    private String statusCode;
    private String statusName;
    private String requestId;
    private Timestamp requestTime;

}
