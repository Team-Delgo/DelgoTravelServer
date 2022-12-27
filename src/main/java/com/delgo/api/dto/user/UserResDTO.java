package com.delgo.api.dto.user;

import com.delgo.api.domain.coupon.Coupon;
import com.delgo.api.domain.pet.Pet;
import com.delgo.api.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class UserResDTO {
    private User user;
    private Pet pet;
    private List<Coupon> couponList;

    public UserResDTO(User user, Pet pet){
        this.user = user;
        this.pet = pet;
    }
}
