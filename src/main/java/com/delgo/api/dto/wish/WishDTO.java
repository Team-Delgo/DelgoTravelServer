package com.delgo.api.dto.wish;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class WishDTO {
    @NotNull
    private Integer userId;
    @NotNull
    private Integer placeId;
}
