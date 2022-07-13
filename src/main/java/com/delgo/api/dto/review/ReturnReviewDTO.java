package com.delgo.api.dto.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ReturnReviewDTO {
    @NotNull
    private List<ReadReviewDTO> readReviewDTOList;
    @NotNull
    private float ratingAvg;

}
