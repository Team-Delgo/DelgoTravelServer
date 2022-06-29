package com.delgo.api.service;

import com.delgo.api.comm.CommService;
import com.delgo.api.comm.exception.ApiCode;
import com.delgo.api.domain.Place;
import com.delgo.api.domain.Room;
import com.delgo.api.domain.booking.Booking;
import com.delgo.api.domain.booking.BookingState;
import com.delgo.api.domain.user.User;
import com.delgo.api.dto.booking.ReturnBookingDTO;
import com.delgo.api.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService extends CommService {

    private final UserService userService;
    private final PlaceService placeService;
    private final RoomService roomService;
    private final CouponService couponService;
    private final PriceService priceService;

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

    public List<Booking> getBookingByUserIdAndBookingState(int userId, BookingState state) {
        return bookingRepository.findByUserIdAndBookingState(userId, state);
    }

    public List<Booking> getBookingByUserId(int userId) {
        return bookingRepository.findByUserId(userId);
    }

    //16자리 생성
    public String createBookingNum() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddhhmmss")) + numberGen(4, 1);
    }

    public ReturnBookingDTO getReturnBookingData(String bookingId) {
        Booking booking = getBookingByBookingId(bookingId);
        Place place = placeService.getPlaceByPlaceId(booking.getPlaceId());
        Room room = roomService.getRoomByRoomId(booking.getRoomId());
        User user = userService.getUserByUserId(booking.getUserId());

        int originalPrice = priceService.getOriginalPrice(booking.getRoomId(), booking.getStartDt(), booking.getEndDt());
        int point = booking.getPoint();
        int couponPrice = (booking.getCouponId() == 0) ? 0 : couponService.getCouponPrice(booking.getCouponId(), originalPrice);
        int finalPrice = originalPrice - point - couponPrice;

        // TODO: 취소 마감일 (?) 결제일기준이아니라 예약날짜 기준 전날 전전날 - 숙소마다 다르다라.. 흐음....(?)
        String canCancelDate = LocalDate.now().plusDays(5).toString();

        return ReturnBookingDTO.builder()
                .bookingId(bookingId)
                .userName(user.getName())
                .userPhoneNo(user.getPhoneNo())
                .placeName(place.getName())
                .placeAddress(place.getAddress())
                .roomName(room.getName())
                .originalPrice(formatIntToPrice(originalPrice))
                .point(point)
                .couponId(booking.getCouponId())
                .couponPrice(formatIntToPrice(couponPrice))
                .finalPrice(formatIntToPrice(finalPrice))
                .startDt(booking.getStartDt())
                .endDt(booking.getEndDt())
                .canCancelDate(canCancelDate)
                .bookingState(booking.getBookingState())
                .build();
    }
}
