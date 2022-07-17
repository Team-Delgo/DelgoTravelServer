package com.delgo.api.domain.room;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int roomId;
    private int placeId;
    private String name;
    private int personMaxNum;
    private int personStandardNum;
    private int petMaxNum;
    private int petStandardNum;
    private String petSizeLimit;
    @CreationTimestamp
    @JsonIgnore
    private LocalDate registDt;
    @JsonIgnore
    private String crawlingUrl;

    @Transient
    private String price;
    @Transient
    private int isBooking;
    @Transient
    private String mainPhotoUrl;
//    @Transient
//    private List<DetailPhoto> detailPhotos;
}
