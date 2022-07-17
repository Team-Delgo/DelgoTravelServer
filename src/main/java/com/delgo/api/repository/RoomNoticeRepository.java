package com.delgo.api.repository;

import com.delgo.api.domain.room.RoomNotice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface RoomNoticeRepository extends JpaRepository<RoomNotice, Integer>, JpaSpecificationExecutor<RoomNotice> {
    List<RoomNotice> findByRoomId(int roomId);
}