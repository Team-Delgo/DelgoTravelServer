package com.delgo.api.repository;

import com.delgo.api.domain.coupon.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Integer> {
    List<Coupon> findByUserId(int userId);

    Optional<Coupon> findByCouponId(int couponId);

    Optional<Coupon> findByUserIdAndCouponManageId(int userId, int couponId);
}
