package com.delgo.api.dto.user;

import com.delgo.api.domain.booking.Booking;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class InfoDTO {
    private String profileUrl;
    private String petName;
    private int couponNum;
    private int reviewNum;
    private List<Booking> bookingList;
}
