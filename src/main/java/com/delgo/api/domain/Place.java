package com.delgo.api.domain;

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
public class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int placeId;
    private String name;
    private String address;
    @CreationTimestamp
    @JsonIgnore
    private LocalDate registDt;
    private String checkin;
    private String checkout;

    @Transient
    private String mainPhotoUrl;
    @Transient
    private int wishId = 0;
    @Transient
    private int isBooking = 0;
    @Transient
    private String lowestPrice;
}
