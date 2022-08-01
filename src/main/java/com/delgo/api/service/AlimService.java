package com.delgo.api.service;

import com.delgo.api.comm.ncp.service.AlimTalkService;
import com.delgo.api.domain.booking.Booking;
import com.delgo.api.domain.place.Place;
import com.delgo.api.domain.room.Room;
import com.delgo.api.domain.user.User;
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
    private final PlaceService placeService;
    private final UserService userService;
    private final RoomService roomService;

    public void sendWaitAlimTalk(Booking booking) throws IOException {
        Place place = placeService.getPlaceByPlaceId(booking.getPlaceId());
        Room room = roomService.getRoomByRoomId(booking.getRoomId());
        User user = userService.getUserByUserId(booking.getUserId());

        try {
            String content = "[Delgo] 예약대기 안내\n" +
                    "아래 숙소의 예약 대기가 접수 되었습니다.\n" +
                    "숙소이름 : " + place.getName() + "\n" +
                    "객실타입 : " + room.getName() + " (기준인원 " + room.getPersonStandardNum() + " 명)\n" +
                    "입실일시: " + booking.getStartDt() + " " + place.getCheckin() + "~\n" +
                    "퇴실일시: " + booking.getEndDt() + " " + place.getCheckout() + "\n" +
                    "\n" +
                    "예약 확정 여부는 숙소 확인 후 문자로 발송됩니다.\n" +
                    "(영업시간 이외 접수된 예약대기는 다음날 오전 중 문자로 발송됩니다.\n" +
                    "\n" +
                    "이제는 델고 가요!";
            alimTalkService.sendAlimTalk("WaitAlim", user.getPhoneNo(), content);
        } catch (Exception e) {
            throw new IOException();
        }
    }

    public void sendCancelWaitAlimTalk(String phoneNo, String placeName, String roomName, String peopleNum, String startDt, String checkIn, String endDt, String checkOut) throws IOException {

        try {
            String content = "[Delgo] 취소 안내\n" +
            "아래 숙소의 취소가 접수되었습니다.\n" +
            "숙소이름 : " + placeName + "\n" +
            "객실타입 : " + roomName + " (기준인원 " + peopleNum + " 명)\n" +
            "입실일시: " + startDt + " " + checkIn + " ~\n" +
            "퇴실일시: " + endDt + " " + checkOut + "\n\n" +

            "취소 확정 여부는 해당 숙소에 취소 요청 확인 후 카카오톡 알림으로 발송됩니다.\n" +
            "(영업시간 이외 접수된 취소요청은 다음날 오전 중 문자로 발송됩니다.\n\n" +

            "이제는 델고 가요!";

            alimTalkService.sendAlimTalk("CancelWait", phoneNo, content);
        } catch (Exception e) {
            throw new IOException();
        }
    }



}
