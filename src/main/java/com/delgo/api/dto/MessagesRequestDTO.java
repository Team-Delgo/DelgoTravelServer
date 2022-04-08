package com.delgo.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Data
@Service
@AllArgsConstructor
@NoArgsConstructor
public class MessagesRequestDTO {
    private String to;
    private String content;
}
