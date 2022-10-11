package com.delgo.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Cancel {
    @Id
    private int cancelId;
    private int placeId;
    private int remainDay;
    private int returnRate;
}
