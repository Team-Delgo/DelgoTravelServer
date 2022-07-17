package com.delgo.api.domain;

import com.delgo.api.domain.photo.ReviewPhoto;
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

    @Transient
    private List<ReviewPhoto> reviewPhotoList;
}
