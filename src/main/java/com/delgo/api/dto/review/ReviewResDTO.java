package com.delgo.api.dto.review;

import com.delgo.api.domain.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ReviewResDTO {
    private Review review;
    private String userName;
    private String placeName;
    private String roomName;
    private String profileUrl;
}
