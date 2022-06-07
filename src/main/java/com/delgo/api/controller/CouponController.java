package com.delgo.api.controller;


import com.delgo.api.comm.CommController;
import com.delgo.api.comm.exception.ApiCode;
import com.delgo.api.domain.coupon.Coupon;
import com.delgo.api.domain.coupon.CouponManager;
import com.delgo.api.dto.CouponManagerDTO;
import com.delgo.api.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

    // Coupon 사용시 사용여부 업데이트
    @PostMapping("/use")
    public ResponseEntity useCoupon(@RequestParam Integer couponId) {
        Optional<Coupon> coupon = couponService.getCouponByCouponId(couponId);
        if (coupon.isEmpty()) return ErrorReturn(ApiCode.COUPON_SELECT_ERROR);

        coupon.get().setIsUsed(1);
        couponService.insertOrUpdateCoupon(coupon.get());

        return SuccessReturn();
    }

    // 쿠폰 등록 API [ 사용자 ]
    @PostMapping("/regist")
    public ResponseEntity registCoupon(
            @RequestParam Integer userId,
            @RequestParam String couponCode) {
        // Validate - Blank Check; [ String 만 해주면 됨 ]
        if (couponCode.isBlank())
            return ErrorReturn(ApiCode.PARAM_ERROR);

        Optional<CouponManager> option = couponService.getCouponManagerByCode(couponCode);
        // ERROR: Coupon Code 잘못된 입력
        if (option.isEmpty()) return ErrorReturn(ApiCode.COUPON_SELECT_ERROR);

        CouponManager cm = option.get();
        // ERROR: 이미 발행된 쿠폰
        if (couponService.checkCouponExisting(userId, cm.getCouponManagerId()))
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
                        .couponManagerId(cm.getCouponManagerId())
                        .userId(userId)
                        .build()
        );

        return SuccessReturn();
    }

    // 쿠폰 관리 등록 API [ 관리자 ]
    @PostMapping("/regist/manager")
    public ResponseEntity registCouponManager(@Validated @RequestBody CouponManagerDTO dto) {
        couponService.insertOrUpdateCouponManager(
                CouponManager.builder()
                        .couponCode(dto.getCouponCode())
                        .couponType(dto.getCouponType())
                        .adminId(dto.getAdminId())
                        .expireDt(dto.getExpireDt())
                        .validDt(dto.getValidDt())
                        .discountNum(dto.getDiscountNum())
                        .build()
        );
       return SuccessReturn();
    }
}