package com.delgo.api.domain.coupon;


import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer couponId;
    private Integer userId;
    private Integer couponManagerId;
    private Boolean isUsed;
    private Boolean isValid; // 1: 사용가능 0: 사용불가능
    @CreationTimestamp
    private LocalDateTime registDt;
    private LocalDate expireDt;
    private String couponType;
    private Integer discountNum;

    public Coupon setIsUsed(boolean isUsed) {
        this.isUsed = isUsed;

        return this;
    }

    public Coupon setIsValid(boolean isValid) {
        this.isValid = isValid;

        return this;
    }
}
