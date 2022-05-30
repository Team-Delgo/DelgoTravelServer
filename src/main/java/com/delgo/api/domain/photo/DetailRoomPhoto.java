package com.delgo.api.domain.photo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

@Data
@Entity
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
    @CreationTimestamp
    @JsonIgnore
    private LocalDate registDt;
    private String url;
    private int isMain;
}
