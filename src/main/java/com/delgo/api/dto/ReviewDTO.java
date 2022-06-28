package com.delgo.api.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
@Builder
public class ReviewDTO {
    @NotNull
    private int reviewId;
    @NotNull
    private int userId;
    @NotNull
    private int placeId;
    @NotNull
    private int roomId;
    @NotNull
    private int rating;
    @NotNull
    private String text;
}
