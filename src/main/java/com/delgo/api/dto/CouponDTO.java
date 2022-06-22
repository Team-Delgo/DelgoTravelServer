package com.delgo.api.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CouponDTO {
    @NotNull
    private Integer userId;
    @NotBlank
    private String couponCode;
}
