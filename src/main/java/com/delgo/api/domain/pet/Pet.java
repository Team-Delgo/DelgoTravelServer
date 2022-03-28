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

    @Column(nullable = false, name="age")
    private int age;

    @Column(nullable = false, name="breed")
    private String breed;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name="size")
    private PetSize size;

    @Column(nullable = false, name="birthday")
    private String birthday;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @CreationTimestamp
    private Timestamp regist_dt;

}