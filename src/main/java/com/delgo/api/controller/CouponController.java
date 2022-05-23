package com.delgo.api.controller;

import com.delgo.api.domain.coupon.Coupon;
import com.delgo.api.domain.coupon.CouponManager;
import com.delgo.api.dto.CouponManagerDTO;
import com.delgo.api.dto.common.ResponseDTO;
import com.delgo.api.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/coupon")
public class CouponController {

    private final CouponService couponService;

    // 각 쿠폰의 정보
    @GetMapping("/getCouponList")
    public ResponseEntity getCouponList(Optional<Integer> userId) {
        if (!userId.isPresent())
            return ResponseEntity.ok().body(
                    ResponseDTO.builder().code(303).codeMsg("Param Error").build());

        // 쿠폰 조회 ( List )
        List<Coupon> list = couponService.getCouponListByUserId(userId.get());

        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(200).codeMsg("Success").data(list).build());
    }

    // Coupon 사용시 사용여부 업데이트
    @PostMapping("/use")
    public ResponseEntity useCoupon(@RequestParam Optional<Integer> couponId) {
        if (!couponId.isPresent())
            return ResponseEntity.ok().body(
                    ResponseDTO.builder().code(303).codeMsg("Param Error").build());

        Optional<Coupon> option = couponService.getCouponByCouponId(couponId.get());
        if (!option.isPresent())
            return ResponseEntity.ok().body(
                    ResponseDTO.builder().code(303).codeMsg("fail").build());

        Coupon coupon = option.get();
        coupon.setIsUsed(1);
        couponService.insertOrUpdateCoupon(coupon);

        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(200).codeMsg("Success").build());
    }

    // 쿠폰 등록 API [ 사용자 ]
    @PostMapping("/regist")
    public ResponseEntity registCoupon(
            @RequestParam Optional<Integer> userId,
            @RequestParam Optional<String> couponCode
    ) {
        if (!userId.isPresent() || !couponCode.isPresent())
            return ResponseEntity.ok().body(
                    ResponseDTO.builder().code(303).codeMsg("Param Error").build());

        Optional<CouponManager> option = couponService.getCouponManagerByCode(couponCode.get());
        // ERROR: Coupon Code 잘못된 입력
        if (!option.isPresent())
            return ResponseEntity.ok().body(
                    ResponseDTO.builder().code(303).codeMsg("Coupon Code is Wrong").build());

        CouponManager cm = option.get();
        // ERROR: 이미 발행된 쿠폰
        if (couponService.checkCouponExisting(userId.get(), cm.getCouponManageId())) {
            return ResponseEntity.ok().body(
                    ResponseDTO.builder().code(303).codeMsg("이미 등록된 쿠폰입니다.").build());
        }

        // 만료 일자 계산
        LocalDate expireDt = LocalDate.now().plusDays(cm.getValidDt());
        if (expireDt.isAfter(cm.getExpireDt())) expireDt = cm.getExpireDt();

        couponService.insertOrUpdateCoupon(
                Coupon.builder()
                        .couponType(cm.getCouponType())
                        .discountNum(cm.getDiscountNum())
                        .expireDt(expireDt)
                        .isUsed(0)
                        .couponManageId(cm.getCouponManageId())
                        .userId(userId.get())
                        .build()
        );

        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(200).codeMsg("Success").build());
    }

    // 쿠폰 등록 API [ 사용자 ]
    // TODO: Null Check 하는법 생각해야 함.
    @PostMapping("/regist/manager")
    public ResponseEntity registCouponManager(@RequestBody CouponManagerDTO dto) {
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

        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(200).codeMsg("Success").build());
    }
}