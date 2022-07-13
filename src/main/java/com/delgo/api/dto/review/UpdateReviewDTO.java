package com.delgo.api.dto.review;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;


@Data
@Builder
public class UpdateReviewDTO {
    private int reviewId;
    private int rating;
    private String text;
    private LocalDate updateDt;
}
