package com.delgo.api.dto.wish;

import com.delgo.api.domain.Wish;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class WishReqDTO {
    @NotNull private Integer userId;
    @NotNull private Integer placeId;

    public Wish toEntity(){
        return  Wish.builder()
                .userId(userId)
                .placeId(placeId)
                .build();
    }
}
