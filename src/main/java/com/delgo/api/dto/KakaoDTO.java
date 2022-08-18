package com.delgo.api.dto;

import com.delgo.api.domain.user.UserSocial;
import lombok.Data;

@Data
public class KakaoDTO {
    private String email = "";
    private String phoneNo = "";
    private UserSocial userSocial;
}
