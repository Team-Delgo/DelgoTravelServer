package com.delgo.api.dto;

import com.delgo.api.domain.place.Place;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class HistoryDTO {
    private String bookingId;
    private Place place;
    private int roomId;
    private String roomName;
    private LocalDate startDt;
    private LocalDate endDt;
    private boolean isReviewExisting; // true = 1, false = 0;
}
