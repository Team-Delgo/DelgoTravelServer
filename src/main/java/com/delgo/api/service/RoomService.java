package com.delgo.api.service;

import com.delgo.api.domain.DetailPhoto;
import com.delgo.api.domain.Room;
import com.delgo.api.repository.DetailPhotoRepository;
import com.delgo.api.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final DetailPhotoRepository detailPhotoRepository;

    public List<Room> selectRoomList(int placeId) {
        List<Room> roomList = roomRepository.findByPlaceId(placeId);
        if (roomList.size() > 0)
            roomList.forEach(room -> {
                //TODO: roomId로 사진 조회해야 함.
                List<DetailPhoto> detailPhotos = detailPhotoRepository.findByRoomId(room.getRoomId());
                room.setDetailPhotos(detailPhotos);
            });

        return roomList;
    }
}
