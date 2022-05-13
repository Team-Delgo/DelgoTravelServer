package com.delgo.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private int roomId;

    @Column(name = "place_id")
    private int placeId;

    private String name;

    @Column(name = "person_max_no")
    private String personMaxNo;

    @Column(name = "pet_max_no")
    private String petMaxNo;

    @Column(name = "pet_sizelimit")
    private String petSizeLimit;

    @CreationTimestamp
    @Column(name = "regist_dt")
    private Timestamp registDt;

    @Column(name = "crawling_url")
    private String crawlingUrl;

    private String checkin;

    private String checkout;

    @Transient
    private String price;

    @Transient
    private int isBooking;

    @Transient
    private List<DetailPhoto> detailPhotos;
}
