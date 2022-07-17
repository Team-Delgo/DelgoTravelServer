package com.delgo.api.domain.place;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class PlaceNotice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int placeNoticeId;
    private int placeId;
    private String title;
    @JsonIgnore
    private String content;


    @Transient
    private List<String> contents;
}
