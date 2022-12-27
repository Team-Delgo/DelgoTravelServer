package com.delgo.api.dto;

import com.delgo.api.domain.photo.DetailRoomPhoto;
import com.delgo.api.domain.place.PlaceNotice;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CalendarResDTO {
    private List<PriceByDateDTO> dateList;
    private List<DetailRoomPhoto> detailRoomPhotos;
    private List<PlaceNotice> roomNoticeList;
}
