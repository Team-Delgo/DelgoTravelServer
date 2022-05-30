package com.delgo.api.dto;

import com.delgo.api.domain.photo.DetailRoomPhoto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CalendarDTO {
    private List<DetailRoomPhoto> detailRoomPhotos;
    private List<DateDTO> dateList;
}
