package com.delgo.api.domain;

import com.delgo.api.domain.photo.ReviewPhoto;
import com.delgo.api.domain.place.Place;
import com.delgo.api.domain.room.Room;
import com.delgo.api.domain.user.User;
import com.delgo.api.dto.review.ReviewResDTO;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;


@Getter
@Entity
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reviewId;
    private int userId;
    private int placeId;
    private int roomId;
    private String bookingId;

    @CreationTimestamp
    private LocalDate registDt;
    private LocalDate updateDt;

    private int rating;
    private String text;

    @Transient
    private List<ReviewPhoto> reviewPhotos;

    public Review setReviewPhotos(List<ReviewPhoto> reviewPhotos){
        this.reviewPhotos = reviewPhotos;

        return this;
    }

    public Review setText(String text){
        this.text = text;

        return this;
    }

    public Review setRating(int rating){
        this.rating = rating;

        return this;
    }

    public ReviewResDTO toResDTO(User user, Place place, Room room) {
        return ReviewResDTO.builder()
                .review(this)
                .userName(user.getName())
                .placeName(place.getName())
                .roomName(room.getName())
                .profileUrl(user.getProfile())
                .build();
    }
}
