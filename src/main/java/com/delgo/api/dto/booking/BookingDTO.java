package com.delgo.api.dto.booking;

import com.delgo.api.domain.booking.Booking;
import com.delgo.api.domain.booking.BookingState;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class BookingDTO {
    @NotNull
    private Integer userId;
    @NotNull
    private Integer roomId;
    @NotNull
    private Integer placeId;
    @NotNull
    private Integer couponId;
    @NotNull
    private Integer point;
    @NotNull
    private Integer personExtraNum;
    @NotNull
    private Integer petExtraNum;
    @NotBlank
    private String startDt;
    @NotBlank
    private String endDt;
    @NotBlank
    private String orderId;
    @NotBlank
    private String paymentKey;


    public Booking build(String bookingId, BookingState bookingState) {
        return Booking.builder()
                .bookingId(bookingId)
                .userId(this.userId)
                .roomId(this.roomId)
                .placeId(this.placeId)
                .couponId(this.couponId)
                .point(this.point)
                .personExtraNum(this.personExtraNum)
                .petExtraNum(this.petExtraNum)
                .startDt(LocalDate.parse(this.startDt))
                .endDt(LocalDate.parse(this.endDt))
                .bookingState(bookingState)
                .orderId(this.orderId)
                .paymentKey(this.paymentKey)
                .build();
    }
}