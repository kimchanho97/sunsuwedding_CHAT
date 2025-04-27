package com.sunsuwedding.chat.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PresenceStatusEvent {
    private Long userId;
    private String chatRoomCode;
    private String status;         // "online"
    private String targetServerUrl;
}
