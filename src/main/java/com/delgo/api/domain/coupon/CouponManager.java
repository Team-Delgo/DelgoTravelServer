package com.delgo.api.domain.coupon;


import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;


@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponManager {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_manage_id")
    private int couponManageId;

    private String couponCode;

    private String couponType;

    private int discountNum;

    private int adminId;

    @CreationTimestamp
    @Column(name = "regist_dt")
    private LocalDate registDt;

    @Column(name = "expire_dt")
    private LocalDate expireDt;

    private int validDt;
}
