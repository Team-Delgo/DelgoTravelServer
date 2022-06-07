package com.delgo.api.service;

import com.delgo.api.domain.booking.Booking;
import com.delgo.api.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;

    public Booking insertOrUpdateBooking(Booking booking){
        return bookingRepository.save(booking);
    }

    public Optional<Booking> getBookingData(int bookingId){
        return bookingRepository.findByBookingId(bookingId);
    }
}
