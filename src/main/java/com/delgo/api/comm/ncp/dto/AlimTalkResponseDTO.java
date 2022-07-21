package com.delgo.api.comm.ncp.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AlimTalkResponseDTO {
    private Timestamp requestTime;
    private String statusCode;
    private String statusName;
    private String requestId;
    private List<AlimTalkMessageDTO> messages;
}
