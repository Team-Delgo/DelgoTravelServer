package com.delgo.api.controller;


import com.delgo.api.comm.CommController;
import com.delgo.api.comm.exception.ApiCode;
import com.delgo.api.domain.coupon.Coupon;
import com.delgo.api.domain.coupon.CouponManager;
import com.delgo.api.dto.CouponDTO;
import com.delgo.api.dto.CouponManagerDTO;
import com.delgo.api.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/coupon")
public class CouponController extends CommController {

    private final CouponService couponService;

    // 각 쿠폰의 정보
    @GetMapping("/getCouponList")
    public ResponseEntity getCouponList(@RequestParam Integer userId) {
        // 쿠폰 조회 ( List )
        List<Coupon> couponList = couponService.getCouponListByUserId(userId);
        return SuccessReturn(couponList);
    }

    // 쿠폰 등록 API [ 사용자 ]
    @PostMapping("/regist")
    public ResponseEntity registCoupon(@Validated @RequestBody CouponDTO dto) {

        CouponManager cm = couponService.getCouponManagerByCode(dto.getCouponCode());
        // ERROR: 이미 발행된 쿠폰
        if (couponService.checkCouponExisting(dto.getUserId(), cm.getCouponManagerId()))
            return ErrorReturn(ApiCode.COUPON_DUPLICATE_ERROR);

        // 만료 일자 계산
        LocalDate expireDt = LocalDate.now().plusDays(cm.getValidDt());
        if (expireDt.isAfter(cm.getExpireDt())) expireDt = cm.getExpireDt();

        couponService.insertOrUpdateCoupon(
                Coupon.builder()
                        .couponType(cm.getCouponType())
                        .discountNum(cm.getDiscountNum())
                        .expireDt(expireDt)
                        .isUsed(0)
                        .isValid(0)
                        .couponManagerId(cm.getCouponManagerId())
                        .userId(dto.getUserId())
                        .build()
        );
        return SuccessReturn();
    }

    // 쿠폰 관리 등록 API [ 관리자 ]
    @PostMapping("/regist/manager")
    public ResponseEntity registCouponManager(@Validated @RequestBody CouponManagerDTO couponManagerDTO) {
        couponService.insertOrUpdateCouponManager(couponManagerDTO.build());

        return SuccessReturn();
    }
}