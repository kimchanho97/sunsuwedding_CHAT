package com.sunsuwedding.chat.model;

import java.time.LocalDateTime;

public record ChatRoomMeta(
        String lastMessage,
        LocalDateTime lastMessageAt,
        Long lastMessageSeqId
) {
    public static ChatRoomMeta empty() {
        return new ChatRoomMeta("", LocalDateTime.MIN, 0L);
    }

}
