package com.delgo.api.comm.ncp.dto.alimTalk;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Data
@Service
@AllArgsConstructor
@NoArgsConstructor
public class AlimTalkButtonDTO {
    private String type;
    private String name;
    private String linkMobile;
    private String linkPc;
}
