package com.delgo.api.domain;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Place {
    @Id
    @Column(name = "place_id")
    private long id;
    private String name;
    private String address;
    @Column(name = "regist_dt")
    private String registDt;
}
