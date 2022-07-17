package com.delgo.api.domain.room;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class RoomNotice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int RoomNoticeId;
    private int roomId;
    private String title;
    private String content;
}
