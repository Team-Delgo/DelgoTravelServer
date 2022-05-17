package com.delgo.api.repository;

import com.delgo.api.domain.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, Integer> {
    List<Coupon> findByUserId(int userId);

    Coupon findByCouponId(int couponId);
}
