package com.delgo.api.service;

import com.delgo.api.dto.CalendarResDTO;
import com.delgo.api.dto.PriceByDateDTO;
import com.delgo.api.service.photo.DetailRoomPhotoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalendarService {

    private final RoomService roomService;
    private final PlaceService placeService;
    private final PriceService priceService;
    private final DetailRoomPhotoService detailRoomPhotoService;

    public CalendarResDTO getCalendar(int roomId) {
        return new CalendarResDTO(
                getPriceByDate(roomId),
                detailRoomPhotoService.getDetailRoomPhotos(roomId),
                placeService.getPlaceNotice(roomService.getRoomById(roomId).getPlaceId())
        );
    }

    // RoomId로 날짜별 가격 조회
    public List<PriceByDateDTO> getPriceByDate(int roomId) {
        return priceService.getPriceByRoomId(roomId).stream().map(p -> {
            String price = p.getPrice()
                    .replace(",", "")
                    .replace("원", "");
            return new PriceByDateDTO(p.getPriceDate(), Integer.parseInt(price), p.getIsBooking());
        }).collect(Collectors.toList());
    }
}
