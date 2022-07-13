package com.delgo.api.dto.review;

import com.delgo.api.domain.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
public class ReadReviewDTO {
    @NotNull
    private Review review;
    @NotNull
    private String userName;
    @NotNull
    private String roomName;
    @NotNull
    private String photoUrl;
}
