package com.delgo.api.controller;


import com.delgo.api.comm.CommController;
import com.delgo.api.comm.exception.ApiCode;
import com.delgo.api.domain.coupon.Coupon;
import com.delgo.api.domain.coupon.CouponManager;
import com.delgo.api.dto.CouponDTO;
import com.delgo.api.dto.CouponManagerDTO;
import com.delgo.api.service.CouponManagerService;
import com.delgo.api.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/coupon")
public class CouponController extends CommController {

    private final CouponService couponService;
    private final CouponManagerService couponManagerService;

    // 쿠폰 등록 API [ 사용자 ]
    @PostMapping
    public ResponseEntity registCoupon(@Validated @RequestBody CouponDTO dto) {
        CouponManager cm = couponManagerService.getCouponManagerByCode(dto.getCouponCode());
        if (couponService.checkCouponExisting(dto.getUserId(), cm.getCouponManagerId()))
            return ErrorReturn(ApiCode.COUPON_DUPLICATE_ERROR); // ERROR: 이미 발행된 쿠폰

        return SuccessReturn(couponService.register(
                Coupon.builder()
                        .couponType(cm.getCouponType())
                        .discountNum(cm.getDiscountNum())
                        .expireDt(couponService.getExpireDate(cm))
                        .isUsed(false)
                        .isValid(true)
                        .couponManagerId(cm.getCouponManagerId())
                        .userId(dto.getUserId())
                        .build()
        ));
    }

    @GetMapping
    public ResponseEntity getCoupon(@RequestParam Integer userId) {
        return SuccessReturn(couponService.getCouponsByUserId(userId));
    }

    // 쿠폰 관리 등록 API [ 관리자 ]
    @PostMapping("/manager")
    public ResponseEntity registCouponManager(@Validated @RequestBody CouponManagerDTO couponManagerDTO) {
        couponManagerService.register(couponManagerDTO.toEntity());
        return SuccessReturn();
    }
}