package com.delgo.api.service;

import com.delgo.api.domain.photo.DetailRoomPhoto;
import com.delgo.api.domain.Room;
import com.delgo.api.domain.price.Price;
import com.delgo.api.repository.DetailRoomPhotoRepository;
import com.delgo.api.repository.PriceRepository;
import com.delgo.api.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public List<Room> selectRoomList(int placeId, String StartDt) {
        List<Room> roomList = roomRepository.findByPlaceId(placeId);
        if (roomList.size() > 0)
            roomList.forEach(room -> {
                //TODO: roomId로 사진 조회해야 함.
                Optional<DetailRoomPhoto> mainPhoto = detailRoomPhotoRepository.findByRoomIdAndIsMain(room.getRoomId(), 1);
                mainPhoto.ifPresent(photo -> room.setMainPhotoUrl(photo.getUrl()));
                //TODO: 가격 조회 및 적용
                Price price = priceRepository.findByPriceDateAndRoomId(StartDt, room.getRoomId());
                room.setPrice(price.getPrice());
                room.setIsBooking(price.getIsBooking());
            });

        return roomList;
    }

    public List<Room> selectAll() {
        return roomRepository.findAll();
    }
}
