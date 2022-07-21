package com.delgo.api.service;

import com.delgo.api.domain.photo.DetailRoomPhoto;
import com.delgo.api.domain.price.Price;
import com.delgo.api.domain.room.Room;
import com.delgo.api.domain.room.RoomNotice;
import com.delgo.api.repository.DetailRoomPhotoRepository;
import com.delgo.api.repository.PriceRepository;
import com.delgo.api.repository.RoomNoticeRepository;
import com.delgo.api.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomNoticeRepository roomNoticeRepository;
    private final PriceRepository priceRepository;
    private final DetailRoomPhotoRepository detailRoomPhotoRepository;

    public List<RoomNotice> getRoomNotice(int roomId) {
        List<RoomNotice> roomNoticeList = roomNoticeRepository.findByRoomId(roomId);
        roomNoticeList.forEach(notice -> {
            String content = notice.getContent();
            String contents[] = content.split("\r\n");
            notice.setContents(Arrays.asList(contents));
        });
        return roomNoticeList;
    }

    public List<Room> selectRoomList(int placeId, LocalDate startDt, LocalDate endDt) {
        Period period = Period.between(startDt, endDt); // 시작, 끝 간격
        List<Room> roomList = roomRepository.findByPlaceId(placeId);
        if (roomList.size() > 0)
            roomList.forEach(room -> {
                // roomId로 사진 조회해야 함.
                Optional<DetailRoomPhoto> mainPhoto =
                        detailRoomPhotoRepository.findByRoomIdAndIsMain(room.getRoomId(), 1);
                mainPhoto.ifPresent(photo -> room.setMainPhotoUrl(photo.getUrl()));
                // 가격 조회 및 적용
                List<Price> pList = priceRepository.findByRoomIdAndIsBookingAndIsWaitAndPriceDateBetween(room.getRoomId(), 0, 0, startDt.toString(), endDt.toString());
                if (pList.size() == period.getDays() + 1) {
                    // 4박5일일 경우 총 여행경비는 앞 4일 가격의 합이다. 따라서 마지막 삭제
                    pList.remove(pList.size() - 1);
                    // 예약가능한 각 방의 가격중 가장 저렴한 가격 조회
                    List<Integer> pricelist = new ArrayList<Integer>();
                    pList.forEach(p -> {
                        String price = p.getPrice();
                        price = price.replace(",", "");
                        price = price.replace("원", "");
                        if (!price.equals("0"))
                            pricelist.add(Integer.parseInt(price));
                    });
                    int sum = pricelist.stream().mapToInt(Integer::intValue).sum();
                    DecimalFormat df = new DecimalFormat("###,###원"); //포맷팅
                    room.setPrice(df.format(sum));
                    room.setIsBooking(0);
                } else {
                    room.setPrice("0원");
                    room.setIsBooking(1);
                }
            });

        return roomList;
    }

    public List<Room> selectAll() {
        return roomRepository.findAll();
    }

    // RoomId로 Room 조회
    public Room getRoomByRoomId(int roomId) {
        return roomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new NullPointerException("NOT FOUND ROOM"));
    }
}
