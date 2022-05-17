package com.delgo.api.repository;

import com.delgo.api.domain.booking.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    Booking findByBookingId(int bookingId);
}
