package com.delgo.api.dto.booking;

import com.delgo.api.domain.Place;
import com.delgo.api.domain.booking.BookingState;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ReturnBookingDTO {
    private String bookingId; // 예약번호
    private String userName; // 유저 이름
    private String userPhoneNo; // 유저 핸드폰 번호
    private String roomName; // 룸타입 ( 이름 )
    private String originalPrice; // 상품금액
    private Integer point; // 포인트 사용금액
    private Integer couponId; // 쿠폰 Id
    private String couponPrice; // 쿠폰 사용 금액
    private String finalPrice; // 최종 금액
    private LocalDate startDt; // 체크인 날짜
    private LocalDate endDt; // 체크아웃 날짜
    private LocalDate registDt; // 등록 날짜
    private String canCancelDate; //  취소 가능한 날짜
    private BookingState bookingState; // 현재 예약 상태 코드
    private Place place;
}