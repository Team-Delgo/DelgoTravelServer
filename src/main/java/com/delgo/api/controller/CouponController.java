package com.delgo.api.controller;

import com.delgo.api.domain.coupon.Coupon;
import com.delgo.api.domain.coupon.CouponManager;
import com.delgo.api.dto.CouponDTO;
import com.delgo.api.dto.CouponManagerDTO;
import com.delgo.api.dto.common.ResponseDTO;
import com.delgo.api.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/coupon")
public class CouponController {

    private final CouponService couponService;

    // TODO: 각 쿠폰의 정보
    // TODO: 7일 이내 소멸예정 쿠폰 알아야 함.
    @GetMapping("/getCouponList")
    public ResponseEntity getCouponList(int userId) {
        // 쿠폰 조회 ( List )
        List<Coupon> list = couponService.getCouponListByUserId(userId);
        List<Coupon> expireList = new ArrayList<Coupon>(); // 7일 뒤 만료 쿠폰 리스트
        List<Coupon> couponList = new ArrayList<Coupon>(); // 그 외 쿠폰 리스트
        LocalDate now = LocalDate.now();

        list.forEach(coupon -> {
            LocalDate expireDt = coupon.getExpireDt();
            if (expireDt.isBefore(now.plusDays(8)))  // 오늘 기준 8일 보다 만료일자가 적은 경우 즉 만료일이 7일 이내인 경우.
                expireList.add(coupon);
            else
                couponList.add(coupon);
        });

        CouponDTO couponDTO = new CouponDTO(couponList, expireList);

        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(200).codeMsg("Success").data(couponDTO).build());
    }

    // Coupon 사용시 사용여부 업데이트
    @PostMapping("/use")
    public ResponseEntity useCoupon(@RequestParam int couponId) {
        Optional<Coupon> option = couponService.getCouponByCouponId(couponId);
        if (!option.isPresent()) {
            return ResponseEntity.ok().body(
                    ResponseDTO.builder().code(303).codeMsg("fail").build());
        }

        Coupon coupon = option.get();
        coupon.setIsUsed(1);
        couponService.insertOrUpdateCoupon(coupon);

        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(200).codeMsg("Success").build());
    }

    // TODO: 쿠폰 등록 API [ 사용자 ]
    @PostMapping("/regist")
    public ResponseEntity registCoupon(@RequestParam int userId, @RequestParam String couponCode) {
        // 1. 쿠폰 코드로 AdminCoupon에서 해당 코드가 실제로 있는지 확인

        Optional<CouponManager> option = couponService.getCouponManagerByCode(couponCode);
        // 쿠폰 코드 잘 못 입력했을 때
        if (!option.isPresent()) {
            // 2. 없으면 에러 코드 반환 ( 쿠폰 코드가 없다 )
            return ResponseEntity.ok().body(
                    ResponseDTO.builder().code(303).codeMsg("쿠폰 코드 잘못 입력").build());
        } else {
            //4. manageCouponId로 Coupon Table에 이미 있는지 확인 (유저마다 한개만 생성할 수 잇음)

            // 5. 있으면 AdminCoupon에서 필요한 데이터 가져와서 쿠폰 만듬.
            CouponManager cm = option.get();
//            if (couponService.checkCouponExisting(userId, cm.getCouponManageId())) {
//                return ResponseEntity.ok().body(
//                        ResponseDTO.builder().code(303).codeMsg("이미 등록된 쿠폰입니다.").build());
//            }
            // 만료 일자 계산 해야 함.
            LocalDate expireDt = LocalDate.now().plusDays(cm.getValidDt());
            if (expireDt.isAfter(cm.getExpireDt()))
                expireDt = cm.getExpireDt();
            Coupon coupon = Coupon.builder()
                    .couponType(cm.getCouponType())
                    .discountNum(cm.getDiscountNum())
                    .expireDt(expireDt)
                    .isUsed(0)
                    .couponManageId(cm.getCouponManageId())
                    .userId(userId)
                    .build();
            // 5. 쿠폰 쿠폰 DB에 넣기
            couponService.insertOrUpdateCoupon(coupon);

            return ResponseEntity.ok().body(
                    ResponseDTO.builder().code(200).codeMsg("Success").build());
        }


    }

    // TODO: 쿠폰 등록 API [ 사용자 ]
    @PostMapping("/regist/manager")
    public ResponseEntity registCouponManager(@RequestBody CouponManagerDTO dto) {
        // 1. adminCoupon 값 받아서 DB에 저장
        CouponManager couponManager = CouponManager.builder()
                .couponCode(dto.getCouponCode())
                .couponType(dto.getCouponType())
                .adminId(dto.getAdminId())
                .expireDt(dto.getExpireDt())
                .validDt(dto.getValidDt())
                .discountNum(dto.getDiscountNum())
                .build();

        couponService.insertOrUpdateCouponManager(couponManager);

        return ResponseEntity.ok().body(
                ResponseDTO.builder().code(200).codeMsg("Success").build());
    }
}