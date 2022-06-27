package com.delgo.api.dto;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class ReviewDTO {
    private int reviewId;
    private int userId;
    private int placeId;
    private int roomId;
    private int rating;
    private String text;
}
