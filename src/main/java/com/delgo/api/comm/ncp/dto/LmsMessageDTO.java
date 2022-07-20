package com.delgo.api.comm.ncp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Data
@Service
@AllArgsConstructor
@NoArgsConstructor
public class LmsMessageDTO {
    private String to;
    private String subject;
    private String content;
}
