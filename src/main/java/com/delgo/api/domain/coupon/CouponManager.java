package com.delgo.api.domain.coupon;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponManager {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_manager_id")
    private Integer couponManagerId;
    private String couponCode;
    private String couponType;
    private Integer discountNum;
    private Integer adminId;
    @CreationTimestamp
    private LocalDateTime registDt;
    private LocalDate expireDt;
    private Integer validDt;
}
