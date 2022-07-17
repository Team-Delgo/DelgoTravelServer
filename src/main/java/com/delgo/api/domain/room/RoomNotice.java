package com.delgo.api.domain.room;

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
public class RoomNotice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int RoomNoticeId;
    private int roomId;
    private String title;
    @JsonIgnore
    private String content;
    @Transient
    private List<String> contents;
}
