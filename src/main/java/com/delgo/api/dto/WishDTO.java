package com.delgo.api.dto;

import com.delgo.api.domain.Wish;
import lombok.Data;

@Data
public class WishDTO {
    private Wish wish;
    private int wishId;
    private int userId;
    private int placeId;
}
