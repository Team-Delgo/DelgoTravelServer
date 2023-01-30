package com.delgo.api.domain.room;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Entity
@ToString
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

    public RoomNotice setContents(List<String> contents){
        this.contents = contents;

        return this;
    }
}
