package com.delgo.api.domain.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private int bookingId;

    @Column(name = "people_num")
    private int peopleNum;

    @Column(name = "pet_num")
    private int petNum;

    @Column(name = "start_dt")
    private LocalDate startDt;

    @Column(name = "end_dt")
    private LocalDate endDt;

    @Enumerated(EnumType.STRING)
    @Column(name = "booking_state")
    private BookingState bookingState;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "room_id")
    private int roomId;

    @Column(name = "place_id")
    private int placeId;
}
