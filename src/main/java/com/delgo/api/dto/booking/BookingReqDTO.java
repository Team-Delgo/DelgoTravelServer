package com.delgo.api.dto.booking;

import com.delgo.api.domain.booking.Booking;
import com.delgo.api.domain.booking.BookingState;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class BookingReqDTO {
    @NotNull private Integer userId;
    @NotNull private String reservedName;
    @NotNull private Integer roomId;
    @NotNull private Integer placeId;
    @NotNull private Integer couponId;
    @NotNull private Integer point;
    @NotNull private LocalDate startDt;
    @NotNull private LocalDate endDt;
    @NotBlank private String orderId;
    @NotBlank private String paymentKey;


    public Booking toEntity(BookingState bookingState, String bookingId, String reservedPhoneNo) {
        return Booking.builder()
                .bookingId(bookingId)
                .userId(userId)
                .reservedName(reservedName)
                .reservedPhoneNo(reservedPhoneNo)
                .roomId(roomId)
                .placeId(placeId)
                .couponId(couponId)
                .point(point)
                .startDt(startDt)
                .endDt(endDt)
                .bookingState(bookingState)
                .orderId(orderId)
                .paymentKey(paymentKey)
                .build();
    }
}