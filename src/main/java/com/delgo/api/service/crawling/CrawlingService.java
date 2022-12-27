package com.delgo.api.service.crawling;

import com.delgo.api.domain.photo.DetailPhoto;
import com.delgo.api.domain.photo.DetailRoomPhoto;
import com.delgo.api.domain.place.Place;
import com.delgo.api.domain.room.Room;
import com.delgo.api.service.*;
import com.delgo.api.service.crawling.place.GetPlaceCrawlingService;
import com.delgo.api.service.crawling.place.GetRoomListCrawlingService;
import com.delgo.api.service.crawling.room.GetRoomCrawlingService;
import com.delgo.api.service.photo.DetailPhotoService;
import com.delgo.api.service.photo.DetailRoomPhotoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CrawlingService {

    private final RoomService roomService;
    private final PlaceService placeService;
    private final DetailPhotoService detailPhotoService;
    private final DetailRoomPhotoService detailRoomPhotoService;

    // Crawling Service
    private final GetRoomCrawlingService getRoomCrawlingService;
    private final GetPriceCrawlingService getPriceCrawlingService;
    private final GetPlaceCrawlingService getPlaceCrawlingService;
    private final GetPhotosCrawlingService getPhotosCrawlingService;
    private final GetRoomListCrawlingService getRoomListCrawlingService;

    public Place register(String crawlingUrl) {

        Place place = getPlaceCrawlingService.crawlingProcess(crawlingUrl); // Place 조회
        registerDetailPhotos(place.getPlaceId(), crawlingUrl);  // Place Photo 등록

        // Room Url List 조회
        List<String> roomUrls = getRoomListCrawlingService.crawlingProcess(crawlingUrl);

        // Room 등록
        List<Room> roomList = roomUrls.stream().map(url -> {
            Room room = registerRoom(place.getPlaceId(), url);
            registerDetailRoomPhotos(place.getPlaceId(), room.getRoomId(), url);

            return room;
        }).collect(Collectors.toList());

        // Room 가격 데이터 크롤링
        getPriceCrawlingService.crawlingProcess(roomList);

        return placeService.register(place);
    }
    public Room registerRoom(int placeId, String crawlingUrl) {
        Room room = getRoomCrawlingService.crawlingProcess(placeId, crawlingUrl);
        return roomService.register(room);
    }

    public void registerDetailPhotos(int placeId, String crawlingUrl) {
        List<String> detailPhotoUrls = getPhotosCrawlingService.crawlingProcess(crawlingUrl);

        List<DetailPhoto> detailPhotos = detailPhotoUrls.stream()
                .map(url -> DetailPhoto.builder()
                        .placeId(placeId)
                        .url(url)
                        .isMain(false)
                        .build())
                .collect(Collectors.toList());

        // 첫 번째 사진 Main으로 등록
        detailPhotos.get(0).setIsMain(true);
        detailPhotoService.registerDetailPhotos(detailPhotos);
    }

    public void registerDetailRoomPhotos(int placeId, int roomId, String crawlingUrl) {
        List<String> photoUrlList = getPhotosCrawlingService.crawlingProcess(crawlingUrl);

        List<DetailRoomPhoto> detailRoomPhotoList = photoUrlList.stream()
                .map(url -> DetailRoomPhoto.builder()
                        .placeId(placeId)
                        .roomId(roomId)
                        .url(url)
                        .isMain(false)
                        .build())
                .collect(Collectors.toList());

        // 첫 번째 사진 Main으로 등록
        detailRoomPhotoList.get(0).setIsMain(true);
        detailRoomPhotoService.registerDetailRoomPhotos(detailRoomPhotoList);
    }
}
