package com.delgo.api.dto;

import com.delgo.api.domain.coupon.Coupon;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@Data
public class CouponDTO {

    public CouponDTO(List<Coupon> couponList, List<Coupon> expireList) {
        this.couponList = couponList;
        this.expireList = expireList;
    }

    private List<Coupon> couponList; // 7일 뒤 만료 쿠폰 리스트
    private List<Coupon> expireList; // 그 외 쿠폰 리스트
}
