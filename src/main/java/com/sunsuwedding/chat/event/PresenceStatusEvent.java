package com.sunsuwedding.chat.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PresenceStatusEvent {
    private Long userId;
    private String chatRoomCode;
    private String status;         // "online"
    private String targetServerUrl;
}
