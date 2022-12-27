package com.delgo.api.service;

import com.delgo.api.domain.price.Price;
import com.delgo.api.domain.room.Room;
import com.delgo.api.domain.room.RoomNotice;
import com.delgo.api.repository.RoomNoticeRepository;
import com.delgo.api.repository.RoomRepository;
import com.delgo.api.service.photo.DetailRoomPhotoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomNoticeRepository roomNoticeRepository;

    private final PriceService priceService;
    private final DetailRoomPhotoService detailRoomPhotoService;

    public Room register(Room room){
       return roomRepository.save(room);
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Room getRoomById(int roomId) {
        return roomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new NullPointerException("NOT FOUND ROOM"));
    }

    public List<RoomNotice> getRoomNotice(int roomId) {
       return roomNoticeRepository.findByRoomId(roomId).stream().map(notice -> {
            String[] contents = notice.getContent().split("\r\n");
            return notice.setContents(Arrays.asList(contents));
        }).collect(Collectors.toList());
    }

    public List<Room> getRoomsByPlace(int placeId, LocalDate startDt, LocalDate endDt) {
        return roomRepository.findByPlaceId(placeId).stream().peek(room -> {
            List<Price> canBookingDates = priceService.getCanBookingDates(room.getRoomId(), startDt, endDt);
            if (canBookingDates.size() == Period.between(startDt, endDt).getDays() + 1) {
                canBookingDates.remove(canBookingDates.size() - 1);  // 4박5일일 경우 총 여행경비는 앞 4일 가격의 합이다. 따라서 마지막 삭제
                int sum = canBookingDates.stream().mapToInt(Price::priceToInt).sum();  // 예약가능한 각 방의 가격중 가장 저렴한 가격 조회

                room.setPrice(new DecimalFormat("###,###원").format(sum));
                room.setIsBooking(false);
            } else {
                room.setPrice("0원");
                room.setIsBooking(true);
            }

            room.setMainPhotoUrl(detailRoomPhotoService.getMainPhoto(room.getRoomId()));  // SET MAIN PHOTO
        }).collect(Collectors.toList());
    }
}
