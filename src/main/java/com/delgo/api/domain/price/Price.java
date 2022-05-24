package com.delgo.api.domain.price;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(PriceId.class) // 다중 PK 설정
public class Price {
    @Id
    @Column(name = "price_date")
    private String priceDate;

    @Id
    @Column(name = "room_id")
    private int roomId;

    @Column(name = "place_id")
    private int placeId;

    @Column(name = "is_booking")
    private int isBooking;

    @Column(name = "is_wait")
    private int isWait;

    private String price;
}
