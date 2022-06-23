package com.delgo.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class InfoDTO {
    private String profileUrl;
    private String petName;
    private int couponNum;
    private int reviewNum;
}
