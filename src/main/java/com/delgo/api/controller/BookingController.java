package com.delgo.api.controller;


import com.delgo.api.comm.CommController;
import com.delgo.api.comm.CommService;
import com.delgo.api.comm.exception.ApiCode;
import com.delgo.api.comm.ncp.service.LmsService;
import com.delgo.api.domain.booking.Booking;
import com.delgo.api.domain.booking.BookingState;
import com.delgo.api.domain.user.User;
import com.delgo.api.dto.HistoryDTO;
import com.delgo.api.dto.booking.BookingDTO;
import com.delgo.api.dto.booking.ReturnBookingDTO;
import com.delgo.api.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/booking")
public class BookingController extends CommController {

    private String ADMIN_PHONE_NO = "01077652211";

    private final CommService commService;
    private final CancelService cancelService;
    private final UserService userService;
    private final CouponService couponService;
    private final BookingService bookingService;
    private final PriceService priceService;
    private final PlaceService placeService;
    private final LmsService lmsService;

    /**
     * 예약 요청 API
     * 쿠폰, 포인트 우선 DB에서 사용 처리 ( 취소 될 경우 [예약 확정 OR 취소 API에서 롤백]
     */
    @PostMapping("/request")
    public ResponseEntity bookingRequest(@Validated @RequestBody BookingDTO bookingDTO) {
        // Validate - 날짜 차이가 2주 이내인가? 시작날짜가 오늘보다 같거나 큰가? 종료날짜는 만료날짜랑 같거나 작은가?
        if (!commService.checkDate(bookingDTO.getStartDt().toString(), bookingDTO.getEndDt().toString()))
            return ErrorReturn(ApiCode.PARAM_DATE_ERROR);
        // Validate - 예약가능한 날짜인가?
        if(!placeService.checkCanBooking(bookingDTO.getPlaceId(),bookingDTO.getStartDt(),bookingDTO.getEndDt()))
            return ErrorReturn(ApiCode.ALREADY_BOOKING_PLACE);

        // TODO: 쿠폰 사용 표시
        if (bookingDTO.getCouponId() != 0) {
            // TODO: Validate - 쿠폰이 유효한 쿠폰인가?
            couponService.couponUse(bookingDTO.getCouponId());
        }
        // TODO: Point 사용 표시
        if (bookingDTO.getPoint() != 0) {
            // TODO: DB 에 Point 관련 로직 짜야 함.
        }
        // 예약 요청 정보를 Booking Table에 저장 , booking_status - W[wait]
        Booking booking = bookingDTO.build(bookingService.createBookingNum(), BookingState.W);
        Booking savedBooking = bookingService.insertOrUpdateBooking(booking);

        //Price Table에 예약 대기중 표시
        priceService.changeToReserveWait(bookingDTO.getStartDt(), bookingDTO.getEndDt());

        // User에게 대기요청문자 발송 [ 문자 어떻게 들어가야 할지 생각 필요 ]
        User user = userService.getUserByUserId(savedBooking.getUserId());
        try {
            // TODO: 운영진에게 예약요청문자 발송 [ 내용 어떤 거 들어갈지 생각 필요 ]
//            lmsService.sendLMS(ADMIN_PHONE_NO, "ADMIN 테스트", "예약요청이 들어왔습니다.");
//
//            // TODO: 사용자에게 예약대기문자 발송 [ 내용 어떤 거 들어갈지 생각 필요 ]
//            lmsService.sendLMS(user.getPhoneNo(), "테스트 제목", "예약완료 될 때까지 기다려주세요.");
        } catch (Exception e) {
            e.printStackTrace();
            return ErrorReturn(ApiCode.SMS_ERROR);
        }

        return SuccessReturn(savedBooking.getBookingId());
    }

    /**
     * 예약 내용 조회 : 메인페이지
     */
    @GetMapping("/getData/main")
    public ResponseEntity getBookingListByMain(@RequestParam Integer userId) {
        if (userId == 0)
            return ErrorReturn(ApiCode.NOT_FOUND_DATA);

        List<Booking> fixList = bookingService.getBookingByUserIdAndBookingState(userId, BookingState.F);
        List<Booking> tripList = bookingService.getBookingByUserIdAndBookingState(userId, BookingState.T);

        if (fixList.isEmpty() && tripList.isEmpty()) // 조회되는 BOOKING DATA 없음
            return ErrorReturn(ApiCode.NOT_FOUND_DATA);
        //정렬 기준 1. 시작 날짜, 2. 종료 날짜
        Comparator<Booking> compare = Comparator
                .comparing(Booking::getStartDt)
                .thenComparing(Booking::getEndDt);

        List<ReturnBookingDTO> returnFixList =
                fixList.stream().sorted(compare).map(b -> bookingService.getReturnBookingData(b.getBookingId())).collect(Collectors.toList());
        List<ReturnBookingDTO> returnTripList =
                tripList.stream().sorted(compare).map(b -> bookingService.getReturnBookingData(b.getBookingId())).collect(Collectors.toList());

        return SuccessReturn(Stream.concat(returnTripList.stream(), returnFixList.stream()).collect(Collectors.toList()));
    }

    /**
     * Histroy 조회 : 여행 완료 Booking Data
     */
    @GetMapping("/getHistory")
    public ResponseEntity getHistoryList(@RequestParam Integer userId) {
        if (userId == 0)
            return ErrorReturn(ApiCode.NOT_FOUND_DATA);

        List<Booking> bookingList = bookingService.getBookingByUserIdAndBookingState(userId, BookingState.E);
        List<HistoryDTO> historyList = new ArrayList<>();
        if (bookingList.isEmpty()) // 조회되는 BOOKING DATA 없음
            return SuccessReturn(historyList);

        bookingList.forEach(booking -> {
            historyList.add(bookingService.getHistoryData(booking.getBookingId()));
        });

        return SuccessReturn(historyList.stream().sorted(Comparator.comparing(HistoryDTO::getStartDt).reversed()));
    }


    /**
     * 예약 내용 조회 : 예약확인 페이지
     */
    @GetMapping("/getData")
    public ResponseEntity getBookingList(@RequestParam String bookingId) {
        return SuccessReturn(bookingService.getReturnBookingData(bookingId));
    }

    // TODO: 취소 요청 API
    @PostMapping(value = {"/cancel/{bookingId}", "/cancel"})
    public ResponseEntity cancelRequest(@PathVariable String bookingId) {
        log.info(bookingId);
        Booking booking = bookingService.getBookingByBookingId(bookingId);
        // TODO: 현재 날짜 vs 여행 날짜 비교 ( 남은 날짜 비교 취소 퍼센트 비교를 위해 )
        LocalDate tripDay = booking.getStartDt();
        if (tripDay.isBefore(LocalDate.now()))
            ErrorReturn(ApiCode.PARAM_DATE_ERROR);

//        Period period = Period.between(LocalDate.now(), tripDay);
//        Cancel cancel = cancelService.getCancelByPlaceIdAndRemainDay(booking.getPlaceId(), period.getDays());
//        int returnRate = cancel.getReturnRate();

//        if(returnRate == 0)
        // TODO: 환불률 0%면 취쇼요청 Cancel

        // TODO: Booking State CW로 변경
        booking.setBookingState(BookingState.CW);
        bookingService.insertOrUpdateBooking(booking);

        // TODO: 사용자 OR 운영진 카톡 발송
        // User 조회
        User user = userService.getUserByUserId(booking.getUserId());
//        log.info(user.toString());
        try {
            // TODO: 사용자에게 취소대기문자 발송 [ 내용 어떤 거 들어갈지 생각 필요 ]
//            smsService.sendSMS(user.getPhoneNo(), "예약완료 될 때까지 기다려주세요.");

            // TODO: 운영진에게 취소문자 발송  내용 어떤 거 들어갈지 생각 필요 ]
//            smsService.sendSMS(user.getPhoneNo(), "예약요청이 들어왔습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ErrorReturn(ApiCode.SMS_ERROR);
        }

        return SuccessReturn();
    }
}