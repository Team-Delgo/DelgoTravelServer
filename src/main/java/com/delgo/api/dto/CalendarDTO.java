package com.delgo.api.dto;

import com.delgo.api.domain.photo.DetailRoomPhoto;
import com.delgo.api.domain.place.PlaceNotice;
import com.delgo.api.domain.room.RoomNotice;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CalendarDTO {
    private List<DetailRoomPhoto> detailRoomPhotos;
    private List<DateDTO> dateList;
    private List<RoomNotice> roomNoticeList;
}
