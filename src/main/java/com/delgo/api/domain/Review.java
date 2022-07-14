package com.delgo.api.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private int reviewId;
    @Column(name = "user_id")
    private int userId;
    @Column(name = "place_id")
    private int placeId;
    @Column(name = "room_id")
    private int roomId;

    @CreationTimestamp
    @Column(name = "regist_dt")
    private LocalDate registDt;
    @Column(name = "update_dt")
    private LocalDate updateDt;

    private int rating;
    private String text;

    private String reviewPhoto1;
    private String reviewPhoto2;
    private String reviewPhoto3;
    private String reviewPhoto4;
    private String reviewPhoto5;

}
