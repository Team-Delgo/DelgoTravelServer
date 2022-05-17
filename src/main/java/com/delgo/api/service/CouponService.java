package com.delgo.api.service;

import com.delgo.api.domain.Coupon;
import com.delgo.api.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    public List<Coupon> getCouponList(int userId) {
        return couponRepository.findByUserId(userId);
    }

    public Coupon getCouponByCouponId(int couponId) {
        return couponRepository.findByCouponId(couponId);
    }


    public Coupon insertOrUpdateCoupon(Coupon coupon) {
        return couponRepository.save(coupon);
    }


}
