package com.delgo.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsAuthDTO {
    @NotNull
    private String randNum;
    @NotNull
    private String auth_time;
}
