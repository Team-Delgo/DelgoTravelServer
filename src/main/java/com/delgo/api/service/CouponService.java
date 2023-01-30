package com.delgo.api.service;

import com.delgo.api.comm.CommService;
import com.delgo.api.domain.coupon.Coupon;
import com.delgo.api.domain.coupon.CouponManager;
import com.delgo.api.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CouponService extends CommService {

    private final CouponRepository couponRepository;

    public Coupon register(Coupon coupon) {
        return couponRepository.save(coupon);
    }

    public Coupon getCouponById(int couponId) {
        return couponRepository.findByCouponId(couponId)
                .orElseThrow(() -> new NullPointerException("NOT FOUND COUPON"));
    }

    public List<Coupon> getCouponsByUserId(int userId) {
        return couponRepository.findByUserIdAndIsUsedAndIsValid(userId, 0, 1);
    }

    public void deleteExpiredCoupon(String yesterday) {
        couponRepository.saveAll(couponRepository.findByExpireDt(yesterday).stream()
                .map(coupon -> coupon.setIsValid(false))
                .collect(Collectors.toList()));
    }

    public boolean checkCouponExisting(int couponId, int couponManagerId) {
        return couponRepository.findByUserIdAndCouponManagerId(couponId, couponManagerId).isPresent();
    }

    public Coupon couponUse(int couponId) {
        return register(getCouponById(couponId).setIsUsed(true));
    }

    public Coupon couponRollback(int couponId) {
        return register(getCouponById(couponId).setIsUsed(false));
    }

    public int getCouponPrice(int couponId, int originalPrice) {
        Coupon coupon = getCouponById(couponId);
        return coupon.getCouponType().equals("P")
                ? originalPrice / 100 * coupon.getDiscountNum() //  % 할인
                : coupon.getDiscountNum(); //coupon.getCouponType() == "N"  Number 할인
    }
    // 만료 일자 계산
    public LocalDate getExpireDate(CouponManager cm){
        LocalDate expireDt = LocalDate.now().plusDays(cm.getValidDt());
        return (expireDt.isAfter(cm.getExpireDt())) ? cm.getExpireDt() : expireDt;
    }
}
