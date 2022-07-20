package com.delgo.api.service;

import com.delgo.api.comm.ncp.service.LmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SendLmsService {
    private final LmsService lmsService;

    public void sendLms(String phoneNo, String subject, String message) throws IOException {
        try {
            lmsService.sendLMS(phoneNo, subject, message);
        } catch (Exception e) {
            throw new IOException();
        }
    }

}
