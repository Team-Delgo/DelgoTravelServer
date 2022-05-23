package com.delgo.api.service;

import com.delgo.api.domain.coupon.Coupon;
import com.delgo.api.domain.coupon.CouponManager;
import com.delgo.api.repository.CouponManagerRepository;
import com.delgo.api.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponManagerRepository couponManagerRepository;

    public List<Coupon> getCouponListByUserId(int userId) {
        return couponRepository.findByUserId(userId);
    }

    public Optional<Coupon> getCouponByCouponId(int couponId) {
        return couponRepository.findByCouponId(couponId);
    }

    public boolean checkCouponExisting(int couponId, int couponManageId) {
        Optional<Coupon> option = couponRepository.findByUserIdAndCouponManageId(couponId, couponManageId);
        return option.isPresent();
    }

    public Coupon insertOrUpdateCoupon(Coupon coupon) {
        return couponRepository.save(coupon);
    }


    // ------------------------------------- Coupon Manager -------------------------------------
    public CouponManager insertOrUpdateCouponManager(CouponManager couponManager) {
        return couponManagerRepository.save(couponManager);
    }

    public Optional<CouponManager> getCouponManagerByCode(String couponCode) {
        return couponManagerRepository.findByCouponCode(couponCode);
    }

}
