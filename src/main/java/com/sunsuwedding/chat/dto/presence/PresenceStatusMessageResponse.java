package com.sunsuwedding.chat.dto.presence;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PresenceStatusMessageResponse {
    private Long userId;
    private String status; // "online"
}
