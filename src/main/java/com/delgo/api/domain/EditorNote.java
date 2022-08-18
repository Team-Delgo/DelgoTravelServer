package com.delgo.api.domain;


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
public class EditorNote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int editorNoteId;
    private int placeId;
    private String url;
    private String thumbnailUrl;
    private String thumbnailTitle;
    private String thumbnailSubtitle;
    @JsonIgnore
    @CreationTimestamp
    private LocalDate registDt;
    private int order;
}
