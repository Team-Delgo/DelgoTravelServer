package com.delgo.api.domain.pet;

import com.delgo.api.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="pet_id")
    private int pet_id;

    @Column(nullable = false, name="name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name="size")
    private PetSize size;

    @Column(nullable = false, name="birthday")
    private String birthday;

    @Column(nullable = false, name="user_id")
    private int user_id;

    @CreationTimestamp
    private Timestamp regist_dt;

}