package com.delgo.api.service;

import com.delgo.api.comm.CommService;
import com.delgo.api.domain.coupon.CouponManager;
import com.delgo.api.repository.CouponManagerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CouponManagerService extends CommService {

    private final CouponManagerRepository couponManagerRepository;

    public CouponManager register(CouponManager couponManager) {
        return couponManagerRepository.save(couponManager);
    }

    public CouponManager getCouponManagerByCode(String couponCode) {
        return couponManagerRepository.findByCouponCode(couponCode)
                .orElseThrow(() -> new NullPointerException("WRONG COUPON CODE")); // ERROR: Coupon Code 잘못된 입력
    }

}
