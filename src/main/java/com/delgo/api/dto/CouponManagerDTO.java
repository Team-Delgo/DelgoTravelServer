package com.delgo.api.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
public class CouponManagerDTO {
    @NotBlank
    private String couponCode;
    @NotBlank
    private String couponType;
    @NotNull
    private Integer discountNum;
    private Integer adminId; // 나중에 NotNull 추가
    @NotNull
    private LocalDate expireDt;
    @NotNull
    private Integer validDt;
}
