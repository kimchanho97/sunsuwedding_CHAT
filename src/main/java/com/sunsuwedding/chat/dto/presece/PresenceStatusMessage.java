package com.sunsuwedding.chat.dto.presece;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PresenceStatusMessage {
    private Long userId;
    private String status; // "online"
}
