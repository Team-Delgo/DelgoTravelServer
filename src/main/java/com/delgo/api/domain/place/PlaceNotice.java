package com.delgo.api.domain.place;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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
    private String content;
}
