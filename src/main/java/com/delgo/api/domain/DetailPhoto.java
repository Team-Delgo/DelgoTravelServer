package com.delgo.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class DetailPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detail_photo_id")
    private int detailPhotoId;

    @Column(name = "place_id")
    private int placeId;

    @Column(name = "room_id")
    private int roomId;

    @CreationTimestamp
    @Column(name = "regist_dt")
    private LocalDate registDt;

    private String url;
}
