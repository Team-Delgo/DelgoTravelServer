package com.delgo.api.controller;


import com.delgo.api.domain.booking.Booking;
import com.delgo.api.domain.booking.BookingState;
import com.delgo.api.domain.user.User;
import com.delgo.api.dto.BookingDTO;
import com.delgo.api.dto.common.ResponseDTO;
import com.delgo.api.service.BookingService;
import com.delgo.api.service.UserService;
import com.delgo.api.config.ncp.smsCertified.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/booking")
public class BookingController {

    private final BookingService bookingService;
    private final SmsService smsService;
    private final UserService userService;

    // TODO: 예약 요청 API [ 여기서 쿠폰 사용 여부 설정 해줄까? ]
    @PostMapping("/request")
    public ResponseEntity bookingRequest(@RequestBody BookingDTO dto) {
        // TODO: 예약 요청 정보를 Booking Table에 저장 , booking_status - W[wait]
        dto.getBooking().setBookingState(BookingState.W);
        log.info(dto.getBooking().toString());
        Booking savedBooking = bookingService.insertOrUpdateBooking(dto.getBooking());

        User user = userService.findByUserId(savedBooking.getUserId());
        log.info(user.toString());
        // TODO: User에게 대기요청문자 발송 [ 문자 어떻게 들어가야 할지 생각 필요 ]
        try {
            smsService.sendSMS(user.getPhoneNo(), "예약완료 될 때까지 기다려주세요.");

        // TODO: 운영진에게 예약요청문자 발송 [ 내용 어떤 거 들어갈지 생각 필요 ]
            smsService.sendSMS(user.getPhoneNo(), "예약요청이 들어왔습니다.");
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(200).codeMsg("Success").data(savedBooking).build()
        );
    }

    // TODO: 예약 확정 API
    @PostMapping("/confirm")
    public ResponseEntity bookingConfirm(@RequestParam int bookingId) {
        // TODO: booking_status - F[fix] 로 변경
        Booking booking = bookingService.getBookingData(bookingId);
        booking.setBookingState(BookingState.F);
        bookingService.insertOrUpdateBooking(booking);

        // TODO: [예약 성공 ]
        // TODO: User에게 예약확정문자 발송

        // TODO: [예약 실패 ]
        // TODO: User에게 예약실패문자 발송

        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(200).codeMsg("Success").data("").build()
        );
    }

    // TODO: 취소 요청 API
    @PostMapping("/cancel")
    public ResponseEntity cancelRequest(@RequestParam int bookingId) {
        // TODO: 취소 정책 생각해야 함.

        // TODO: 운영진에게 취소내용 발송

        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(200).codeMsg("Success").data("").build()
        );
    }

    // TODO: 취소 확정 API
    @PostMapping("/cancel/confirm")
    public ResponseEntity cancelConfirm(@RequestParam int bookingId) {
        // TODO: booking_status - C[cancel] 로 변경
        Booking booking = bookingService.getBookingData(bookingId);
        booking.setBookingState(BookingState.C);
        bookingService.insertOrUpdateBooking(booking);

        // TODO: User에게 취소문자 발송

        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(200).codeMsg("Success").data("").build()
        );
    }
}