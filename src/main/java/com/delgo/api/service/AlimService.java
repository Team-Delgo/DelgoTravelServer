package com.delgo.api.service;

import com.delgo.api.comm.ncp.service.AlimTalkService;
import com.delgo.api.domain.pet.PetSize;
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

    public void sendWaitAlimTalk(String templateCode, String phoneNo, String placeName, String roomName, String startDt, String endDt) throws IOException {
        try {
            String content = "[Delgo] 예약대기 안내\n" +
                    "아래 숙소의 예약 대기가 접수 되었습니다.\n" +
                    "숙소이름 : " + placeName + "\n" +
                    "객실타입 : " + roomName + " (기준인원2명)\n" +
                    "입실일시: " + startDt + " 15:00 ~\n" +
                    "퇴실일시: " + endDt + " 11:00\n" +
                    "\n" +
                    "예약 확정 여부는 숙소 확인 후 문자로 발송됩니다.\n" +
                    "(영업시간 이외 접수된 예약대기는 다음날 오전 중 문자로 발송됩니다.\n" +
                    "\n" +
                    "이제는 델고 가요!";
            alimTalkService.sendAlimTalk(templateCode, phoneNo, content);
        } catch (Exception e) {
            throw new IOException();
        }
    }

    public void sendFixAlimTalk(String templateCode, String phoneNo, String userName, String placeName, String roomName, String startDt, String endDt) throws IOException {
        try {
            String content = "[Delgo] 예약확정 안내\n" +
                    "안녕하세요? " + userName +"님의 예약이 확정되었습니다.\n" +
                    "아래 숙소의 예약 정보를 확인해주세요.\n" +
                    "\n" +
                    "숙소이름 : " + placeName + "\n" +
                    "객실타입 : " + roomName + " (기준인원2명)\n" +
                    "입실일시: " + startDt + " 15:00 ~\n" +
                    "퇴실일시: " + endDt + " 11:00\n" +
                    "\n" +
                    "반려견과 좋은 시간 보내세요!";
            alimTalkService.sendAlimTalk(templateCode, phoneNo, content);
        } catch (Exception e) {
            throw new IOException();
        }
    }

    public void sendCancelAlimTalk(String templateCode, String phoneNo, String placeName, String roomName, String price, String payment) throws IOException {
        try {
            String content = "[Delgo] 예약취소 안내\n" +
                    "안녕하세요? Delgo입니다.\n" +
                    "고객님께서 예약하신 숙소가 취소되었습니다.\n" +
                    "\n" +
                    "숙소이름 : " + placeName + "\n" +
                    "객실타입 : " + roomName + " (기준인원2명)\n" +
                    "환불예정금액: " + price + "원\n" +
                    "결제수단: " + payment + "\n" +
                    "환불소요기간: 영업일 기준 3~5일\n" +
                    "\n" +
                    "이용해 주셔서 감사합니다.";
            alimTalkService.sendAlimTalk(templateCode, phoneNo, content);
        } catch (Exception e) {
            throw new IOException();
        }
    }

}
