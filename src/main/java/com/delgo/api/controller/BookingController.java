package com.delgo.api.controller;


import com.delgo.api.comm.CommController;
import com.delgo.api.comm.CommService;
import com.delgo.api.comm.exception.ApiCode;
import com.delgo.api.comm.ncp.service.SmsService;
import com.delgo.api.domain.booking.Booking;
import com.delgo.api.domain.booking.BookingState;
import com.delgo.api.domain.user.User;
import com.delgo.api.dto.booking.BookingDTO;
import com.delgo.api.dto.booking.ReturnBookingDTO;
import com.delgo.api.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/booking")
public class BookingController extends CommController {

    private final CommService commService;
    private final UserService userService;
    private final CouponService couponService;
    private final BookingService bookingService;
    private final SmsService smsService;

    /**
     * 예약 요청 API
     * 쿠폰, 포인트 우선 DB에서 사용 처리 ( 취소 될 경우 [예약 확정 OR 취소 API에서 롤백]
     */
    @PostMapping("/request")
    public ResponseEntity bookingRequest(@Validated @RequestBody BookingDTO bookingDTO) {
        // Validate - 날짜 차이가 2주 이내인가? 시작날짜가 오늘보다 같거나 큰가? 종료날짜는 만료날짜랑 같거나 작은가?
        if (!commService.checkDate(bookingDTO.getStartDt(), bookingDTO.getEndDt()))
            return ErrorReturn(ApiCode.PARAM_DATE_ERROR);

        // TODO: Price Table에 예약 대기중 표시

        // TODO: 쿠폰 사용 표시
        if (bookingDTO.getCouponId() != 0) {
            // TODO: Validate - 쿠폰이 유효한 쿠폰인가?
            // TODO: Quartz 필요 ( 정각에 마감기한에 따라 쿠폰 가능 여부 isValid 업데이트 )
            couponService.couponUse(bookingDTO.getCouponId());
        }
        // TODO: Point 사용 표시
        if (bookingDTO.getPoint() != 0) {
            // TODO: DB 에 Point 관련 로직 짜야 함.
        }
        // TODO: 예약 번호 생성 ( booking_id )

        // 예약 요청 정보를 Booking Table에 저장 , booking_status - W[wait]
        Booking booking = bookingDTO.build(bookingService.createBookingNum(), BookingState.W);
        Booking savedBooking = bookingService.insertOrUpdateBooking(booking);

        // User에게 대기요청문자 발송 [ 문자 어떻게 들어가야 할지 생각 필요 ]
        User user = userService.getUserByUserId(savedBooking.getUserId());
        try {
            // TODO: 사용자에게 예약대기문자 발송 [ 내용 어떤 거 들어갈지 생각 필요 ]
//            smsService.sendSMS(user.getPhoneNo(), "예약완료 될 때까지 기다려주세요.");

            // TODO: 운영진에게 예약요청문자 발송 [ 내용 어떤 거 들어갈지 생각 필요 ]
//            smsService.sendSMS(user.getPhoneNo(), "예약요청이 들어왔습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ErrorReturn(ApiCode.SMS_ERROR);
        }

        return SuccessReturn(savedBooking.getBookingId());
    }

    /**
     *  예약 내용 조회 : 메인페이지
     */
    @GetMapping("/getData/main")
    public ResponseEntity getBookingListByMain(@RequestParam Integer userId) {
        if (userId == 0)
            return ErrorReturn(ApiCode.NOT_FOUND_DATA);

        List<Booking> waitList = bookingService.getBookingByUserIdAndBookingState(userId, BookingState.W);
        List<Booking> fixList = bookingService.getBookingByUserIdAndBookingState(userId, BookingState.F);
        if (waitList.isEmpty() && fixList.isEmpty()) // 조회되는 BOOKING DATA 없음
            return ErrorReturn(ApiCode.NOT_FOUND_DATA);
        //정렬 기준 1. 시작 날짜, 2. 종료 날짜
        Comparator<Booking> compare = Comparator
                .comparing(Booking::getStartDt)
                .thenComparing(Booking::getEndDt);

        List<ReturnBookingDTO> returnWaitList = waitList.stream().sorted(compare).map(b -> bookingService.getReturnBookingData(b.getBookingId())).collect(Collectors.toList());
        List<ReturnBookingDTO> returnFixList = fixList.stream().sorted(compare).map(b -> bookingService.getReturnBookingData(b.getBookingId())).collect(Collectors.toList());

        return SuccessReturn(Stream.concat(returnFixList.stream(), returnWaitList.stream()).collect(Collectors.toList()));
    }


    /**
     *  예약 내용 조회 : 예약확인 페이지
     */
    @GetMapping("/getData")
    public ResponseEntity getBookingList(@RequestParam String bookingId) {
        return SuccessReturn(bookingService.getReturnBookingData(bookingId));
    }

    //TODO: 예약 확정 API
//    @PostMapping("/confirm")
//    public ResponseEntity bookingConfirm(@RequestParam Integer bookingId) {
//        // TODO: booking_status - F[fix] 로 변경
//        Optional<Booking> booking = bookingService.getBookingData(bookingId);
//        if (booking.isEmpty())
//            return ErrorReturn(ApiCode.BOOKING_NOT_EXIST);
//
//        booking.get().setBookingState(BookingState.F);
//        bookingService.insertOrUpdateBooking(booking.get());
//
//        // TODO: [예약 성공 ]
//        // TODO: User에게 예약확정문자 발송
//
//        // TODO: [예약 실패 ]
//        // TODO: User에게 예약실패문자 발송
//
//        return SuccessReturn();
//    }
//

//    // TODO: 취소 요청 API
//    @PostMapping("/cancel")
//    public ResponseEntity cancelRequest(@RequestParam Integer bookingId) {
//        // TODO: 취소 정책 생각 ( Defalut : 숙소의 취소정책 따라감 )
//
//        Booking booking = bookingService.getBookingByBookingId(bookingId);
//
//        booking.setBookingState(BookingState.C);
//        bookingService.insertOrUpdateBooking(booking);
//        // TODO: 사용자에게 취소 실패 OR 성공 문자 발송
//
//        // User 조회
//        User user = userService.getUserByUserId(booking.getUserId());
//        log.info(user.toString());
//        try {
//            // TODO: 사용자에게 예약대기문자 발송 [ 내용 어떤 거 들어갈지 생각 필요 ]
////            smsService.sendSMS(user.getPhoneNo(), "예약완료 될 때까지 기다려주세요.");
//
//            // TODO: 운영진에게 취소소문자 발송  내용 어떤 거 들어갈지 생각 필요 ]
////            smsService.sendSMS(user.getPhoneNo(), "예약요청이 들어왔습니다.");
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ErrorReturn(ApiCode.SMS_ERROR);
//        }
//
//
//        return SuccessReturn();
//    }

    // TODO: 취소 확정 API
//    @PostMapping("/cancel/confirm")
//    public ResponseEntity cancelConfirm(@RequestParam int bookingId) {
//        // TODO: booking_status - C[cancel] 로 변경
//        Booking booking = bookingService.getBookingData(bookingId);
//        booking.setBookingState(BookingState.C);
//        bookingService.insertOrUpdateBooking(booking);
//
//        // TODO: User에게 취소문자 발송
//
//        return ResponseEntity.ok().body(
//                ResponseDTO.builder().code(200).codeMsg("Success").data("").build()
//        );
//    }

}