package com.delgo.api.dto.wish;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DeleteWishDTO {
    @NotNull
    private Integer wishId;
}
