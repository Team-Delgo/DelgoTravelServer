package com.delgo.api.dto.place;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class PlaceResDTO {
    private int placeId;
    private String name;
    private String address;
    private String checkin;
    private String checkout;
    private String conceptTag;
    private String phoneNo;
    private String mapUrl;
    private LocalDate registDt;

    private int wishId;
    private int isBooking;
    private String lowestPrice;
    private String mainPhotoUrl;

    public PlaceResDTO setMainPhotoUrl(String mainPhotoUrl){
        this.mainPhotoUrl = mainPhotoUrl;

        return this;
    }
}
