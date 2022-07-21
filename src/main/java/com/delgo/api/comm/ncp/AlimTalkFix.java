package com.delgo.api.comm.ncp;

import com.delgo.api.domain.pet.PetSize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Data
@Service
@AllArgsConstructor
@NoArgsConstructor
public class AlimTalkFix {
    private String userName;
    private String place;
    private String room;
    private PetSize petSize;
    private String option;
    private String startDt;
    private String endDt;
}
