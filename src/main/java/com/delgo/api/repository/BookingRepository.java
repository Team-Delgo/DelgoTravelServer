package com.delgo.api.repository;

import com.delgo.api.domain.booking.Booking;
import com.delgo.api.domain.booking.BookingState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    Optional<Booking> findByBookingId(String bookingId);

    List<Booking> findByBookingIdAndBookingState(String bookingId, BookingState bookingState);

    List<Booking> findByUserIdAndBookingState(int userId, BookingState state);

    List<Booking> findByUserId(int userId);

    List<Booking> findByStartDt(String today);
    List<Booking> findByEndDt(String today);
}
