package com.delgo.api.service;

import com.delgo.api.domain.Wish;
import com.delgo.api.repository.WishRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class WishService {

    private final WishRepository wishRepository;

    public Wish register(Wish wish){
        return wishRepository.save(wish);
    }

    public List<Wish> getWishByUserId(int userId) {
        return wishRepository.findByUserIdOrderByRegistDtDesc(userId);
    }

    public int delete(int wishId){
         return wishRepository.deleteByWishId(wishId);
    }
}
