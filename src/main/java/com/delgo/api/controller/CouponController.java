package com.delgo.api.controller;

import com.delgo.api.domain.Coupon;
import com.delgo.api.dto.CouponDTO;
import com.delgo.api.dto.common.ResponseDTO;
import com.delgo.api.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/coupon")
public class CouponController {

    private final CouponService couponService;

    @GetMapping("/getCouponList")
    public ResponseEntity getCouponList(int userId) {
        // 쿠폰 조회 ( List )
        List<Coupon> couponList = couponService.getCouponList(userId);

        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(200).codeMsg("Success").data(couponList).build());
    }

    // Coupon 사용시 사용여부 업데이트
    @PostMapping("/use")
    public ResponseEntity useCoupon(@RequestParam int couponId) {
        Coupon coupon = couponService.getCouponByCouponId(couponId);
        coupon.setIsUsed(1);
        couponService.insertOrUpdateCoupon(coupon);

        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(200).codeMsg("Success").build());
    }

    // TODO: 쿠폰 등록 API [ 나중에 쿠폰 정책 정해져야 함. ]
    // Coupon 사용시 사용여부 업데이트
    @PostMapping("/regist")
    public ResponseEntity registCoupon(@RequestBody CouponDTO dto) {
        couponService.insertOrUpdateCoupon(dto.getCoupon());

        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(200).codeMsg("Success").build());
    }

}