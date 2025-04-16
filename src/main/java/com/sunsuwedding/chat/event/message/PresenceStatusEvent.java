package com.sunsuwedding.chat.event.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PresenceStatusEvent {
    private Long userId;
    private String status; // "online"
    private String serverId;
}
