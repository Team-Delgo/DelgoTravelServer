package com.delgo.api.dto.review;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;


@Data
@Builder
public class CreateReviewDTO {
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
    private List<String> reviewPhotoList;
}
