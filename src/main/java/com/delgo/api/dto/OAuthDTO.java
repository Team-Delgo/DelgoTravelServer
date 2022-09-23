package com.delgo.api.dto;

import com.delgo.api.domain.user.UserSocial;
import lombok.Data;

@Data
public class OAuthDTO {
    private String email = "";
    private String phoneNo = "";
    private UserSocial userSocial;
}
