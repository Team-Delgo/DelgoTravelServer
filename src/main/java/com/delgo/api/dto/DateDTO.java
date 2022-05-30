package com.delgo.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DateDTO {
    private String date;
    private String price;
    private int isBooking;
}
