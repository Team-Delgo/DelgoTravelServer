package com.delgo.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_id")
    private int placeId;
    private String name;
    private String address;

    @CreationTimestamp
    @Column(name = "regist_dt")
    private Timestamp registDt;

    @Column(name = "main_photo_url")
    private String mainPhotoUrl;

    @Transient
    private String lowestPrice;

    @Transient
    private int wishId = 0;
}
