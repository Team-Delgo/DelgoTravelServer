package com.delgo.api.dto;

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
    private Integer peopleNum;
    @NotNull
    private Integer petNum;
    @NotBlank
    private String startDt;
    @NotBlank
    private String endDt;


    public Booking build(BookingState bookingState) {
        return Booking.builder()
                .userId(this.userId)
                .roomId(this.roomId)
                .placeId(this.placeId)
                .peopleNum(this.peopleNum)
                .petNum(this.petNum)
                .startDt(LocalDate.parse(this.startDt))
                .endDt(LocalDate.parse(this.endDt))
                .bookingState(bookingState)
                .build();

    }
}