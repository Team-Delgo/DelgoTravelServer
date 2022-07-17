package com.delgo.api.repository;

import com.delgo.api.domain.room.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Integer> {
    List<Room> findByPlaceId(int placeId);

    Optional<Room> findByRoomId(int roomId);
}
