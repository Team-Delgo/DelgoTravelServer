package com.delgo.api.dto.review;

import com.delgo.api.domain.Review;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
@Builder
public class ReviewReqDTO {
    @NotNull private int userId;
    @NotNull private int placeId;
    @NotNull private int roomId;
    @NotBlank private String bookingId;
    @NotNull private int rating;
    @NotBlank private String text;

    public Review toEntity() {
        return Review.builder()
                .userId(userId)
                .placeId(placeId)
                .roomId(roomId)
                .bookingId(bookingId)
                .rating(rating)
                .text(text)
                .build();
    }
}
