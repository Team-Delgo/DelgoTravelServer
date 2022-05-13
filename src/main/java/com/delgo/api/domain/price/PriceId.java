package com.delgo.api.domain.price;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceId implements Serializable {
    private String priceDate;
    private int roomId;
}
