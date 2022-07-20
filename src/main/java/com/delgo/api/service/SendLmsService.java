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

    public void sendComplete(String phoneNo) throws IOException {
        String subject = "[Delgo 예약 완료 안내]";
        String message = "내용";
        try {
            lmsService.sendLMS(phoneNo, subject, message);
        } catch (Exception e) {
            throw new IOException();
        }
    }

    public void sendConfirm(String phoneNo) throws IOException {
        String subject = "[Delgo 예약 확정 안내]";
        String message = "내용";
        try {
            lmsService.sendLMS(phoneNo, subject, message);
        } catch (Exception e) {
            throw new IOException();
        }
    }

    public void sendCancel(String phoneNo) throws IOException {
        String subject = "[Delgo 예약 취소 안내]";
        String message = "내용";
        try {
            lmsService.sendLMS(phoneNo, subject, message);
        } catch (Exception e) {
            throw new IOException();
        }
    }
}
