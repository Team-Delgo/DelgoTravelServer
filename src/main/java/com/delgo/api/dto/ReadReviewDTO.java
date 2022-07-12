package com.delgo.api.dto;

import com.delgo.api.domain.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ReadReviewDTO {
    @NotNull
    private List<Review> reviewList;
    @NotNull
    private String userName;
    @NotNull
    private String petPhotoUrl;
    @NotNull
    private String roomName;
}
