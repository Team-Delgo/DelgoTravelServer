package com.delgo.api.domain.coupon;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id")
    private int couponId;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "coupon_manager_id")
    private int couponManagerId;

    @Column(name = "is_used")
    private int isUsed;

    @CreationTimestamp
    @Column(name = "regist_dt")
    private LocalDate registDt;

    @Column(name = "expire_dt ")
    private LocalDate expireDt;

    private String couponType;

    private int discountNum;
}
