package com.delgo.api.dto;

import com.delgo.api.domain.photo.DetailPhoto;
import com.delgo.api.domain.place.Place;
import com.delgo.api.domain.place.PlaceNotice;
import com.delgo.api.domain.room.Room;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailResDTO {
    private Place place;
    private Boolean isEditorNoteExist;
    private List<PlaceNotice> placeNoticeList;
    private List<Room> roomList;
    private List<DetailPhoto> detailPhotoList;
}
