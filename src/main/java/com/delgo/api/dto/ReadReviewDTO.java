package com.delgo.api.dto;

import com.delgo.api.domain.Review;
import com.delgo.api.domain.Room;
import com.delgo.api.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ReadReviewDTO {
    @NotNull
    private List<Review> reviewList;
    @NotNull
    private List<User> userList;
    @NotNull
    private List<Room> roomList;
    @NotNull
    private float ratingAvg;
}
