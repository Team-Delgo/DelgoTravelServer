package com.delgo.api.domain.place;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Entity
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int placeId;
    private String name;
    private String address;
    private String checkin;
    private String checkout;
    private String conceptTag;
    private String phoneNo;
    private String mapUrl;

    @JsonIgnore
    @CreationTimestamp
    private LocalDate registDt;

    @Transient private int wishId;
    @Transient private Boolean isBooking;
    @Transient private String lowestPrice;
    @Transient private String mainPhotoUrl;

    public Place setMainPhotoUrl(String mainPhotoUrl){
        this.mainPhotoUrl = mainPhotoUrl;

        return this;
    }

    public Place setWishId(int wishId){
        this.wishId = wishId;

        return this;
    }
}
