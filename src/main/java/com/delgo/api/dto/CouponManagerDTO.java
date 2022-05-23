package com.delgo.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CouponManagerDTO {
    @NotBlank
    private String couponCode;
    private String couponType;
    private int discountNum;
    private int adminId;
    private LocalDate expireDt;
    private int validDt;
}
