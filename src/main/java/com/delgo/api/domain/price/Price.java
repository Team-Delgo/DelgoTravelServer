package com.delgo.api.domain.price;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Getter
@Entity
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@IdClass(PriceId.class) // 다중 PK 설정
public class Price {
    @Id private String priceDate;
    @Id private Integer roomId;
    private Integer placeId;
    private Boolean isBooking;
    private Boolean isWait;
    private String price;

    public Integer priceToInt(){
       return Integer.parseInt(price.replace(",", "").replace("원", ""));
    }

    public Price setIsBooking(boolean isBooking){
        this.isBooking = isBooking;

        return this;
    }

    public Price setIsWait(boolean isWait){
        this.isWait = isWait;

        return this;
    }
}
