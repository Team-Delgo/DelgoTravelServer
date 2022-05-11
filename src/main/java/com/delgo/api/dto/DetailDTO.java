package com.delgo.api.dto;

import com.delgo.api.domain.Place;
import com.delgo.api.domain.Room;
import lombok.Data;

import java.util.List;

@Data
public class DetailDTO {
    private Place place;
    private List<Room> roomList;
}
