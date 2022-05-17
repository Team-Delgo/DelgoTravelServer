package com.delgo.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Wish {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wish_id")
    private int wishId;
    @Column(name = "user_id")
    private int userId;
    @Column(name = "place_id")
    private int placeId;

    @CreationTimestamp
    @Column(name="regist_dt")
    private LocalDate registDt;
}
