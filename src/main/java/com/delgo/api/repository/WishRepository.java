package com.delgo.api.repository;

import com.delgo.api.domain.Wish;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishRepository extends JpaRepository<Wish, Integer> {
    List<Wish> findByUserIdOrderByRegistDtDesc(int userId);

    int deleteByWishId(int wishId);
}
