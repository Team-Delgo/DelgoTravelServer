package com.delgo.api.repository;

import com.delgo.api.domain.coupon.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Integer> {
    List<Coupon> findByUserId(int userId);

    List<Coupon> findByUserIdAndIsUsedAndIsValid(int userId, int isUsed, int isValid);

    Optional<Coupon> findByCouponId(int couponId);

    Optional<Coupon> findByUserIdAndCouponManagerId(int userId, int couponManagerId);

    List<Coupon> findByExpireDt(String date);
}
