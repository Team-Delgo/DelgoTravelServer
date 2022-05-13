package com.delgo.api.domain.price;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
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

    @Column(name = "isBooking")
    private int isBooking;

    private String price;

}
