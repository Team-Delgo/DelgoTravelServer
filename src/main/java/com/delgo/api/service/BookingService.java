package com.delgo.api.service;

import com.delgo.api.comm.CommService;
import com.delgo.api.comm.exception.ApiCode;
import com.delgo.api.domain.booking.Booking;
import com.delgo.api.domain.booking.BookingState;
import com.delgo.api.domain.place.Place;
import com.delgo.api.domain.room.Room;
import com.delgo.api.dto.HistoryDTO;
import com.delgo.api.dto.booking.BookingReqDTO;
import com.delgo.api.dto.booking.BookingResDTO;
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
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService extends CommService {

    // Service
    private final UserService userService;
    private final PlaceService placeService;
    private final RoomService roomService;
    private final CouponService couponService;
    private final PriceService priceService;
    private final ReviewService reviewService;
    private final CancelService cancelService;
    private final DetailPhotoService detailPhotoService;

    // Repository
    private final BookingRepository bookingRepository;

    public Booking register(Booking booking) {
        return bookingRepository.save(booking);
    }

    public Booking getBookingById(String bookingId) {
        return bookingRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new NullPointerException(ApiCode.NOT_FOUND_DATA.getMsg()));
    }

    public List<Booking> getBookingByUserId(int userId) {
        return bookingRepository.findByUserId(userId);
    }

    public List<Booking> getBookingByBookingState(int userId, BookingState state) {
        return bookingRepository.findByUserIdAndBookingState(userId, state);
    }

    public BookingResDTO getBookingResDTO(String bookingId) {
        Booking booking = getBookingById(bookingId);
        Place place = placeService.getPlaceById(booking.getPlaceId())
                .setMainPhotoUrl(detailPhotoService.getMainPhotoUrl(booking.getPlaceId())); // 사진 설정

        int commission = calculateCommission(booking); // 수수료
        int refund = booking.getFinalPrice() - commission;; // 반환 금액
        int originalPrice = priceService.getOriginalPrice(booking.getRoomId(), booking.getStartDt(), booking.getEndDt()); // 원가
        int point = booking.getPoint(); // 포인트
        int couponPrice = (booking.getCouponId() == 0) ? 0 : couponService.getCouponPrice(booking.getCouponId(), originalPrice); // 쿠폰 가격
        int finalPrice = originalPrice - point - couponPrice; // 최종 가격
        String canCancelDate = booking.getStartDt().minusDays(5).toString(); // 취소 가능 날짜.

        return BookingResDTO.builder()
                .bookingId(bookingId)
                .reservedName(booking.getReservedName())
                .userPhoneNo(userService.getUserById(booking.getUserId()).getPhoneNo())
                .roomName(roomService.getRoomById(booking.getRoomId()).getName())
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

    public List<BookingResDTO> getMainBooking(int userId){
        List<Booking> fixList = getBookingByBookingState(userId, BookingState.F);
        List<Booking> tripList = getBookingByBookingState(userId, BookingState.T);

        // 정렬 기준 1. 시작 날짜, 2. 종료 날짜
        Comparator<Booking> compare = Comparator.comparing(Booking::getStartDt).thenComparing(Booking::getEndDt);
        return Stream.concat(
                        fixList.stream().sorted(compare).map(b -> getBookingResDTO(b.getBookingId())),
                        tripList.stream().sorted(compare).map(b -> getBookingResDTO(b.getBookingId())))
                .collect(Collectors.toList());
    }

    public List<BookingResDTO> getAccount(int userId){
        // 정렬 기준 1. 시작 날짜, 2. 종료 날짜
        Comparator<Booking> compare = Comparator.comparing(Booking::getStartDt).thenComparing(Booking::getEndDt);
        return getBookingByUserId(userId).stream()
                .sorted(compare)
                .map(b -> getBookingResDTO(b.getBookingId()))
                .collect(Collectors.toList());
    }

    public List<HistoryDTO> getHistory(int userId) {
        return getBookingByBookingState(userId, BookingState.E).stream()
                .map(booking -> {
                    Room room = roomService.getRoomById(booking.getRoomId());
                    Place place = placeService.getPlaceById(booking.getPlaceId())
                            .setMainPhotoUrl(detailPhotoService.getMainPhotoUrl(booking.getPlaceId())); // 사진 설정
                    return HistoryDTO.builder()
                            .bookingId(booking.getBookingId())
                            .roomId(room.getRoomId())
                            .roomName(room.getName())
                            .startDt(booking.getStartDt())
                            .endDt(booking.getEndDt())
                            .place(place)
                            .isReviewExisting(reviewService.isReviewExisting(booking.getBookingId()))
                            .build();})
                .sorted(Comparator.comparing(HistoryDTO::getStartDt).reversed())
                .collect(Collectors.toList());
    }

    public void fixToTrip(String today) {
        bookingRepository.saveAll(bookingRepository.findByStartDt(today).stream()
                .peek(booking -> booking.setBookingState(BookingState.T))
                .collect(Collectors.toList())
        );
    }

    public void tripToEnd(String today) {
        bookingRepository.saveAll(bookingRepository.findByStartDt(today).stream()
                .peek(booking -> booking.setBookingState(BookingState.E))
                .collect(Collectors.toList())
        );
    }

    // 16자리 생성
    public String createBookingNum() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddhhmmss")) + numberGen(4, 1);
    }

    // 수수료 계산
    // TODO : 우선은 취소 공동 정책으로 표시 [ 추후 숙소별로 변경 ]
    public int calculateCommission(Booking booking) {
        int period = (int) ChronoUnit.DAYS.between(LocalDate.now(), booking.getStartDt());
        int commission = (period > 14) ? 0 : booking.getFinalPrice();  // 14일 이전에 취소 시 수수료는 0원.
        return !(period <= 14 && period >= 1)
                ? commission  // 당일 취소는 수수료 100%
                : booking.getFinalPrice() / 100 * (100 - cancelService.getCancelById(period).getReturnRate());
    }

    public int calculateFinalPrice(BookingReqDTO reqDTO) {
        int originalPrice = priceService.getOriginalPrice(reqDTO.getRoomId(), reqDTO.getStartDt(), reqDTO.getEndDt());
        int point = reqDTO.getPoint();
        int couponPrice = (reqDTO.getCouponId() == 0) ? 0 : couponService.getCouponPrice(reqDTO.getCouponId(), originalPrice);

        return originalPrice - point - couponPrice;
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
