package com.delgo.api.repository;

import com.delgo.api.domain.SmsAuth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SmsAuthRepository extends JpaRepository<SmsAuth, Integer> {
    Optional<SmsAuth> findBySmsId(int smsId);

    SmsAuth findByPhoneNo(String phoneNo);
}
