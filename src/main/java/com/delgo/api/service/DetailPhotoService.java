package com.delgo.api.service;

import com.delgo.api.repository.DetailPhotoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class DetailPhotoService {

    private final DetailPhotoRepository detailPhotoRepository;


   public String getMainPhotoUrl(int placeId){
       return detailPhotoRepository.findByPlaceIdAndIsMain(placeId, 1)
               .orElseThrow(() -> new NullPointerException("NOT FOUND DETAIL MAIN PHOTO"))
               .getUrl();
   }

}
