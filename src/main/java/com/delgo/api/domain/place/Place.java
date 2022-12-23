package com.delgo.api.domain.place;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
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

    @Transient private int wishId = 0;
    @Transient private int isBooking = 0;
    @Transient private String lowestPrice;
    @Transient private String mainPhotoUrl;

    public Place setMainPhotoUrl(String mainPhotoUrl){
        this.mainPhotoUrl = mainPhotoUrl;

        return this;
    }
}
