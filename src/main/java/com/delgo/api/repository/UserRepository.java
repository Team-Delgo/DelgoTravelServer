package com.delgo.api.repository;

import com.delgo.api.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findById(int user_id);
    User findByEmail(String email);
}
