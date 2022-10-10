package com.delgo.api.service;

import com.delgo.api.comm.CommService;
import com.delgo.api.domain.Cancel;
import com.delgo.api.repository.CancelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CancelService extends CommService {

    private final CancelRepository cancelRepository;

    public Cancel getCancelByPlaceIdAndRemainDay(int placeId, int remainDay) {
        return cancelRepository.findByPlaceIdAndRemainDay(placeId, remainDay)
                .orElseThrow(() -> new NullPointerException("NOT FOUND CANCEL"));
    }

    public Cancel getCancelByCancelId(int cancelId) {
        return cancelRepository.findByCancelId(cancelId)
                .orElseThrow(() -> new NullPointerException("NOT FOUND CANCEL"));
    }
}
