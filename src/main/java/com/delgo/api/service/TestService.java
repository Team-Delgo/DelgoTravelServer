package com.delgo.api.service;

import com.delgo.api.domain.place.Place;
import com.delgo.api.repository.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class TestService {

    private final TestRepository testRepository;

    @Autowired
    public TestService(TestRepository testRepository) {
        this.testRepository = testRepository;
    }

    public Optional<Place> getPlaceData(){
        return testRepository.findOne();
    }
}
