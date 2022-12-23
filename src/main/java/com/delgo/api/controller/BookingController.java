package com.delgo.api.controller;


import com.delgo.api.comm.CommController;
import com.delgo.api.comm.CommService;
import com.delgo.api.comm.exception.ApiCode;
import com.delgo.api.comm.ncp.service.SmsService;
import com.delgo.api.domain.booking.Booking;
import com.delgo.api.domain.booking.BookingState;
import com.delgo.api.dto.HistoryDTO;
import com.delgo.api.dto.booking.BookingReqDTO;
import com.delgo.api.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Period;
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

    private final String ADMIN_PHONE_NO = "01077652211";

    private final CommService commService;
    private final CancelService cancelService;
    private final UserService userService;
    private final CouponService couponService;
    private final BookingService bookingService;
    private final PriceService priceService;
    private final PlaceService placeService;
    private final SmsService smsService;
    private final AlimService alimService;

    /*
     * 예약 요청 API
     * 쿠폰, 포인트 우선 DB에서 사용 처리 ( 취소 될 경우 [예약 확정 OR 취소 API에서 롤백]
     * TODO: Point 사용 표시
     */
    @PostMapping
    public ResponseEntity bookingRequest(@Validated @RequestBody BookingReqDTO reqDTO) {
        // Validate - 날짜 차이가 2주 이내인가? 시작날짜가 오늘보다 같거나 큰가? 종료날짜는 만료날짜랑 같거나 작은가?
        if (!commService.checkDate(reqDTO.getStartDt().toString(), reqDTO.getEndDt().toString()))
            return ErrorReturn(ApiCode.PARAM_DATE_ERROR);
        // Validate - 예약가능한 날짜인가?
        if (!placeService.checkCanBooking(reqDTO.getPlaceId(), reqDTO.getStartDt(), reqDTO.getEndDt()))
            return ErrorReturn(ApiCode.ALREADY_BOOKING_PLACE);

        if (reqDTO.getCouponId() != 0) {
            couponService.couponUse(reqDTO.getCouponId());
        }

        Booking booking = reqDTO.toEntity(
                        BookingState.W,
                        bookingService.createBookingNum(),
                        userService.getUserById(reqDTO.getUserId()).getPhoneNo())
                .setFinalPrice(bookingService.calculateFinalPrice(reqDTO)); // 최종 가격 계산

        //Price Table에 예약 대기중 표시
        priceService.changeToReserveWait(reqDTO.getStartDt(), reqDTO.getEndDt(), 1);

        try {

            String adminMsg = "예약번호 : " + booking.getBookingId() + " 예약요청이 들어왔습니다.";
            smsService.sendSMS(ADMIN_PHONE_NO, adminMsg); // 운영진에게 예약요청문자 발송
            alimService.sendWaitAlimTalk(booking);  // 사용자에게 예약대기문자 발송
        } catch (Exception e) {
            return ErrorReturn(ApiCode.SMS_ERROR);
        }

        return SuccessReturn(bookingService.register(booking).getBookingId());
    }

    /*
     * 예약 내용 조회 : 예약확인 페이지
     */
    @GetMapping
    public ResponseEntity getBooking(@RequestParam String bookingId) {
        return SuccessReturn(bookingService.getBookingResDTO(bookingId));
    }

    /*
     * 예약 내용 조회 : 메인페이지
     */
    @GetMapping("/main")
    public ResponseEntity getMainBooking(@RequestParam Integer userId) {
        List<Booking> fixList = bookingService.getBookingByBookingState(userId, BookingState.F);
        List<Booking> tripList = bookingService.getBookingByBookingState(userId, BookingState.T);

        if (fixList.isEmpty() && tripList.isEmpty()) // 조회되는 BOOKING DATA 없음
            return ErrorReturn(ApiCode.NOT_FOUND_DATA);

        // 정렬 기준 1. 시작 날짜, 2. 종료 날짜
        Comparator<Booking> compare = Comparator.comparing(Booking::getStartDt).thenComparing(Booking::getEndDt);

        return SuccessReturn(Stream.concat(
                        fixList.stream().sorted(compare).map(b -> bookingService.getBookingResDTO(b.getBookingId())),
                        tripList.stream().sorted(compare).map(b -> bookingService.getBookingResDTO(b.getBookingId())))
                .collect(Collectors.toList()));
    }

    /*
     * 예약 내용 조회 : Account
     */
    @GetMapping("/account")
    public ResponseEntity getAccount(@RequestParam Integer userId) {
        List<Booking> bookingList = bookingService.getBookingByUserId(userId);

        if (bookingList.isEmpty())
            return SuccessReturn(bookingList);

        // 정렬 기준 1. 시작 날짜, 2. 종료 날짜
        Comparator<Booking> compare = Comparator.comparing(Booking::getStartDt).thenComparing(Booking::getEndDt);
        return SuccessReturn(bookingList.stream().sorted(compare)
                .map(b -> bookingService.getBookingResDTO(b.getBookingId())).collect(Collectors.toList()));
    }

    /*
     * History 조회 : 여행 완료 Booking Data
     */
    @GetMapping("/history")
    public ResponseEntity getHistory(@RequestParam Integer userId) {
        List<Booking> bookingList = bookingService.getBookingByBookingState(userId, BookingState.E);
        if (bookingList.isEmpty()) // 조회되는 BOOKING DATA 없음
            return SuccessReturn(new ArrayList<>());

        return SuccessReturn(bookingList.stream()
                .map(booking -> bookingService.getHistory(booking.getBookingId()))
                .sorted(Comparator.comparing(HistoryDTO::getStartDt).reversed())
                .collect(Collectors.toList()));
    }

    // TODO: 취소 요청 API
    @PostMapping(value = {"/cancel/{bookingId}", "/cancel"})
    public ResponseEntity cancelRequest(@PathVariable String bookingId) {
        Booking booking = bookingService.getBookingById(bookingId);

        // 오늘 날짜 vs 여행 날짜 비교 ( 남은 날짜 비교 취소 퍼센트 비교를 위해 )
        if (booking.getStartDt().isBefore(LocalDate.now()))
            ErrorReturn(ApiCode.PARAM_DATE_ERROR);

        Period period = Period.between(LocalDate.now(), booking.getStartDt());
        int returnRate = cancelService.getCancelById(Math.min(period.getDays(), 14)).getReturnRate();

        if (returnRate == 0)
            ErrorReturn(ApiCode.REFUND_ZERO);

        try {
            String msg = "예약번호 : " + booking.getBookingId() + " 취소 요청이 들어왔습니다.";
            smsService.sendSMS(ADMIN_PHONE_NO, msg); // 운영진에게 취소요청문자 발송
            alimService.sendCancelWaitAlimTalk(booking); // 사용자에게 취소대기문자 발송
        } catch (Exception e) {
            return ErrorReturn(ApiCode.SMS_ERROR);
        }

        return SuccessReturn(bookingService.register(booking.setBookingState(BookingState.CW)));
    }
}