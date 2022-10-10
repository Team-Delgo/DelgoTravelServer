package com.delgo.api.service;

import com.delgo.api.comm.CommService;
import com.delgo.api.comm.exception.ApiCode;
import com.delgo.api.domain.Cancel;
import com.delgo.api.domain.booking.Booking;
import com.delgo.api.domain.booking.BookingState;
import com.delgo.api.domain.place.Place;
import com.delgo.api.domain.room.Room;
import com.delgo.api.domain.user.User;
import com.delgo.api.dto.HistoryDTO;
import com.delgo.api.dto.booking.ReturnBookingDTO;
import com.delgo.api.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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
    private final ReviewService reviewService;
    private final CancelService cancelService;

    private final BookingRepository bookingRepository;


    public void fixToTrip(String today) {
        List<Booking> bookingList = bookingRepository.findByStartDt(today);
        for (Booking booking : bookingList) {
            booking.setBookingState(BookingState.T);
            bookingRepository.save(booking);
        }
    }

    public void tripToEnd(String today) {
        List<Booking> bookingList = bookingRepository.findByEndDt(today);
        for (Booking booking : bookingList) {
            booking.setBookingState(BookingState.E);
            bookingRepository.save(booking);
        }
    }

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

    public int calculateFinalPrice(Booking booking) {
        int originalPrice = priceService.getOriginalPrice(booking.getRoomId(), booking.getStartDt(), booking.getEndDt());
        int point = booking.getPoint();
        int couponPrice = (booking.getCouponId() == 0) ? 0 : couponService.getCouponPrice(booking.getCouponId(), originalPrice);

        return originalPrice - point - couponPrice;
    }

    public ReturnBookingDTO getReturnBookingData(String bookingId) {
        Booking booking = getBookingByBookingId(bookingId);
        Place place = placeService.getPlaceByPlaceId(booking.getPlaceId());
        placeService.setMainPhoto(place); // 사진 설정
        Room room = roomService.getRoomByRoomId(booking.getRoomId());
        User user = userService.getUserByUserId(booking.getUserId());
        int period = (int) ChronoUnit.DAYS.between(LocalDate.now(), booking.getStartDt());
        int commission = 0;

        if (period <= 14 && period >= 1) {
            // TODO : 우선은 취소 공동 정책으로 표시 [ 추후 숙소별로 변경 ]
//            Cancel cancel = cancelService.getCancelByPlaceIdAndRemainDay(booking.getPlaceId(), period);
            Cancel cancel = cancelService.getCancelByCancelId(period);
            commission = booking.getFinalPrice() / 100 * (100 - cancel.getReturnRate());
        } else if (period > 14) {
            commission = 0;
        } else {
            commission = booking.getFinalPrice();
        }

        int refund = booking.getFinalPrice() - commission;

        int originalPrice = priceService.getOriginalPrice(booking.getRoomId(), booking.getStartDt(), booking.getEndDt());
        int point = booking.getPoint();
        int couponPrice = (booking.getCouponId() == 0) ? 0 : couponService.getCouponPrice(booking.getCouponId(), originalPrice);
        int finalPrice = originalPrice - point - couponPrice;

        String canCancelDate = booking.getStartDt().minusDays(5).toString();

        return ReturnBookingDTO.builder()
                .bookingId(bookingId)
                .reservedName(booking.getReservedName())
                .userPhoneNo(user.getPhoneNo())
                .roomName(room.getName())
                .originalPrice(formatIntToPrice(originalPrice))
                .point(point)
                .couponId(booking.getCouponId())
                .couponPrice(formatIntToPrice(couponPrice))
                .finalPrice(formatIntToPrice(finalPrice))
                .commission(formatIntToPrice(commission))
                .refund(formatIntToPrice(refund))
                .startDt(booking.getStartDt())
                .endDt(booking.getEndDt())
                .canCancelDate(canCancelDate)
                .bookingState(booking.getBookingState())
                .registDt(booking.getRegistDt())
                .place(place)
                .build();
    }

    public HistoryDTO getHistoryData(String bookingId) {
        Booking booking = getBookingByBookingId(bookingId);
        Place place = placeService.getPlaceByPlaceId(booking.getPlaceId());
        placeService.setMainPhoto(place); // 사진 설정
        Room room = roomService.getRoomByRoomId(booking.getRoomId());

        return HistoryDTO.builder()
                .bookingId(bookingId)
                .roomId(room.getRoomId())
                .roomName(room.getName())
                .startDt(booking.getStartDt())
                .endDt(booking.getEndDt())
                .place(place)
                .isReviewExisting(reviewService.isReviewExisting(bookingId))
                .build();
    }

    public HttpResponse<String> requestPaymentCancel(String paymentKey) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel"))
                .header("Authorization", "Basic dGVzdF9za19PeUwwcVo0RzFWT0xvYkI2S3d2cm9XYjJNUVlnOg==")
                .header("Content-Type", "application/json")
                .method("POST", HttpRequest.BodyPublishers.ofString("{\"cancelReason\":\"고객이 취소를 원함\"}"))
                .build();
        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}
