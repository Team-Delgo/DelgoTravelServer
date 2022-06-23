package com.delgo.api.comm.ncp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Data
@Service
@AllArgsConstructor
@NoArgsConstructor
public class AlimTalkRequestDTO {
    private String plusFriendId;
    private String templateCode;
    private List<AlimTalkMessageDTO> messages;
}
