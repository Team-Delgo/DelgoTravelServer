package com.delgo.api.service;

import com.delgo.api.comm.ncp.service.AlimTalkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AlimService {
    private final AlimTalkService alimTalkService;

    public void sendAlimTalk(String templateCode, String phoneNo) throws IOException {
        try {
            alimTalkService.sendAlimTalk(templateCode, phoneNo);
        } catch (Exception e) {
            throw new IOException();
        }
    }

}
