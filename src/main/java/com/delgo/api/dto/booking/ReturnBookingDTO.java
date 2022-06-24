package com.delgo.api.dto.booking;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ReturnBookingDTO {
    private String bookingId; // 예약번호
    private String userName; // 유저 이름
    private String userPhoneNo; // 유저 핸드폰 번호
    private String placeName; // 숙소명
    private String placeAddress; //숙소 주소
    private String roomName; // 룸타입 ( 이름 )
    private String originalPrice; // 상품금액
    private Integer point; // 포인트 사용금액
    private Integer couponId; // 쿠폰 Id
    private String couponPrice; // 쿠폰 사용 금액
    private String finalPrice; // 최종 금액
    private LocalDate startDt; // 체크인 날짜
    private LocalDate endDt; // 체크아웃 날짜
    private String canCancelDate; //  취소 가능한 날짜
}