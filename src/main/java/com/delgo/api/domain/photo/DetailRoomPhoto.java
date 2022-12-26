package com.delgo.api.domain.photo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

@Getter
@Entity
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DetailRoomPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int detailRoomPhotoId;
    @JsonIgnore
    private int placeId;
    @JsonIgnore
    private int roomId;
    @JsonIgnore
    @CreationTimestamp
    private LocalDate registDt;
    private String url;
    private Boolean isMain;
}
