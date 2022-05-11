package com.delgo.api.repository;

import com.delgo.api.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Integer> {
    List<Room> findByPlaceId(int placeId);
}
