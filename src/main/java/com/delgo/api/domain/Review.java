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
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reviewId;
    private int userId;
    private int placeId;
    private int roomId;
    private String bookingId;

    @CreationTimestamp
    private LocalDate registDt;
    private LocalDate updateDt;

    private int rating;
    private String text;

    private String reviewPhoto1;
    private String reviewPhoto2;
    private String reviewPhoto3;
    private String reviewPhoto4;
    private String reviewPhoto5;
}
