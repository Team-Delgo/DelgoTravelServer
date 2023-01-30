package com.delgo.api.service.photo;

import com.delgo.api.domain.photo.DetailRoomPhoto;
import com.delgo.api.repository.photo.DetailRoomPhotoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class DetailRoomPhotoService {

    private final DetailRoomPhotoRepository detailRoomPhotoRepository;

    public List<DetailRoomPhoto> registerDetailRoomPhotos(List<DetailRoomPhoto> detailPhotos){
        return detailRoomPhotoRepository.saveAll(detailPhotos);
    }

    public List<DetailRoomPhoto> getDetailRoomPhotos(int roomId) {
        return detailRoomPhotoRepository.findByRoomId(roomId);
    }

    public String getMainPhoto(int roomId){
        return detailRoomPhotoRepository.findByRoomIdAndIsMain(roomId, true)
                .orElseThrow(() -> new NullPointerException("NOT FOUND MAIN PHOTO BY ROOM"))
                .getUrl();
    }
}
