package com.delgo.api.repository;

import com.delgo.api.domain.SmsAuth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SMSAuthRepository extends JpaRepository<SmsAuth, Integer> {
    Optional<SmsAuth> findBySmsId(int smsId);
}
