package com.delgo.api.service;

import com.delgo.api.comm.CommService;
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
public class CouponService extends CommService {

    private final CouponRepository couponRepository;
    private final CouponManagerRepository couponManagerRepository;

    public List<Coupon> getCouponListByUserId(int userId) {
        return couponRepository.findByUserId(userId);
    }

    public void deleteExpiredCoupon(String yesterday){
        List<Coupon> deleteList = couponRepository.findByExpiredDt(yesterday);
        for(Coupon coupon : deleteList){
            coupon.setIsValid(0);
            couponRepository.save(coupon);
        }
    }

    public Coupon getCouponByCouponId(int couponId) {
        return couponRepository.findByCouponId(couponId)
                .orElseThrow(() -> new NullPointerException("NOT FOUND COUPON"));
    }

    public boolean checkCouponExisting(int couponId, int couponManagerId) {
        Optional<Coupon> option = couponRepository.findByUserIdAndCouponManagerId(couponId, couponManagerId);
        return option.isPresent();
    }

    public Coupon insertOrUpdateCoupon(Coupon coupon) {
        return couponRepository.save(coupon);
    }

    public Coupon couponUse(int couponId) {
        Coupon coupon = getCouponByCouponId(couponId);
        coupon.setIsUsed(1);

        return insertOrUpdateCoupon(coupon);
    }

    public Coupon couponRollback(int couponId) {
        Coupon coupon = getCouponByCouponId(couponId);
        coupon.setIsUsed(0);

        return insertOrUpdateCoupon(coupon);
    }

    public int getCouponPrice(int couponId, int originalPrice) {
        Coupon coupon = getCouponByCouponId(couponId);
        if (coupon.getCouponType() == "P") //  % 할인
            return originalPrice / 100 * coupon.getDiscountNum();

        //coupon.getCouponType() == "N"  Number 할
        return coupon.getDiscountNum();
    }

    // ------------------------------------- Coupon Manager -------------------------------------
    public CouponManager insertOrUpdateCouponManager(CouponManager couponManager) {
        return couponManagerRepository.save(couponManager);
    }

    public CouponManager getCouponManagerByCode(String couponCode) {
        return couponManagerRepository.findByCouponCode(couponCode)
                .orElseThrow(() -> new NullPointerException("WRONG COUPON CODE")); // ERROR: Coupon Code 잘못된 입력
    }

}
