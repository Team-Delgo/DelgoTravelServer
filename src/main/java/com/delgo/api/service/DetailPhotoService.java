package com.delgo.api.service;

import com.delgo.api.domain.photo.DetailPhoto;
import com.delgo.api.repository.DetailPhotoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class DetailPhotoService {

    private final DetailPhotoRepository detailPhotoRepository;

    public List<DetailPhoto> registerDetailPhotos(List<DetailPhoto> detailPhotos){
        return detailPhotoRepository.saveAll(detailPhotos);
    }

   public String getMainPhotoUrl(int placeId){
       return detailPhotoRepository.findByPlaceIdAndIsMain(placeId, 1)
               .orElseThrow(() -> new NullPointerException("NOT FOUND DETAIL MAIN PHOTO"))
               .getUrl();
   }

}
