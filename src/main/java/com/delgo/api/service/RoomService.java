package com.delgo.api.service;

import com.delgo.api.domain.Room;
import com.delgo.api.domain.photo.DetailRoomPhoto;
import com.delgo.api.domain.price.Price;
import com.delgo.api.repository.DetailRoomPhotoRepository;
import com.delgo.api.repository.PriceRepository;
import com.delgo.api.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final PriceRepository priceRepository;
    private final DetailRoomPhotoRepository detailRoomPhotoRepository;

    public List<Room> selectRoomList(int placeId, LocalDate startDt, LocalDate endDt) {
        Period period = Period.between(startDt, endDt); // 시작, 끝 간격
        List<Room> roomList = roomRepository.findByPlaceId(placeId);
        if (roomList.size() > 0)
            roomList.forEach(room -> {
                // roomId로 사진 조회해야 함.
                Optional<DetailRoomPhoto> mainPhoto = detailRoomPhotoRepository.findByRoomIdAndIsMain(room.getRoomId(), 1);
                mainPhoto.ifPresent(photo -> room.setMainPhotoUrl(photo.getUrl()));
                // 가격 조회 및 적용
                List<Price> pList = priceRepository.findByRoomIdAndIsBookingAndPriceDateBetween(room.getRoomId(), 0, startDt.toString(), endDt.toString());
                if (pList.size() == period.getDays() + 1) {
                    room.setPrice(pList.get(0).getPrice());
                    room.setIsBooking(0);
                } else {
                    room.setPrice("0원");
                    room.setIsBooking(1);
                }
            });

        return roomList;
    }

    public List<Room> selectAll() {
        return roomRepository.findAll();
    }
}
