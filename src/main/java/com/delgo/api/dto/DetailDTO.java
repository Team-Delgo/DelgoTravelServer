package com.delgo.api.dto;

import com.delgo.api.domain.photo.DetailPhoto;
import com.delgo.api.domain.Place;
import com.delgo.api.domain.Room;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailDTO {
    private Place place;
    private List<Room> roomList;
    private List<DetailPhoto> detailPhotoList;
}
