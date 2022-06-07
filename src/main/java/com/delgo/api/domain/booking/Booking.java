package com.delgo.api.domain.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bookingId;
    private Integer userId;
    private Integer roomId;
    private Integer placeId;
    private Integer peopleNum;
    private Integer petNum;
    private LocalDate startDt;
    private LocalDate endDt;
    @Enumerated(EnumType.STRING)
    private BookingState bookingState;
}
