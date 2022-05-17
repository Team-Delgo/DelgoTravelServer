package com.delgo.api.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id")
    private int couponId;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "coupon_type")
    private int couponType;

    @CreationTimestamp
    @Column(name = "regist_dt")
    private LocalDate registDt;

    @CreationTimestamp
    @Column(name = "used_dt")
    private LocalDate usedDt;

    @Column(name = "isUsed")
    private String isUsed;
}
