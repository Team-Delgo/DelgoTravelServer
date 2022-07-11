package com.delgo.api.dto;

import com.delgo.api.domain.Place;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class HistoryDTO {
    private String bookingId;
    private Place place;
    private String roomName;
    private LocalDate startDt;
    private LocalDate endDt;
}
