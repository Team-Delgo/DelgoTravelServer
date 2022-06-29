package com.delgo.api.domain.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.time.LocalDate;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    private String bookingId;
    private Integer userId;
    private Integer roomId;
    private Integer placeId;
    private Integer couponId;
    private Integer point;
    private Integer peopleNum;
    private Integer petNum;
    private LocalDate startDt;
    private LocalDate endDt;
    @Enumerated(EnumType.STRING)
    private BookingState bookingState;
    private String orderId; // toss OrderId
    private String paymentKey; // toss 취소할 때 사용.
}
