package com.delgo.api.service;

import com.delgo.api.comm.CommService;
import com.delgo.api.comm.exception.ApiCode;
import com.delgo.api.domain.booking.Booking;
import com.delgo.api.domain.booking.BookingState;
import com.delgo.api.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService extends CommService {

    private final BookingRepository bookingRepository;

    public Booking insertOrUpdateBooking(Booking booking) {
        return bookingRepository.save(booking);
    }

    public Booking getBookingByBookingId(String bookingId) {
        return bookingRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new NullPointerException(ApiCode.NOT_FOUND_DATA.getMsg()));
    }

    public List<Booking> getBookingByBookingIdAndBookingState(String bookingId, BookingState bookingState) {
        return bookingRepository.findByBookingIdAndBookingState(bookingId, bookingState);
    }

    public List<Booking> getBookingByUserId(int userId, BookingState state) {
        return bookingRepository.findByUserIdAndBookingState(userId, state);
    }

    //16자리 생성
    public String createBookingNum() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddhhmmss")) + numberGen(4, 1);
    }
}
