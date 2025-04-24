package com.sunsuwedding.chat.model;

import java.time.LocalDateTime;

public record ChatRoomMeta(
        String lastMessage,
        LocalDateTime lastMessageAt,
        Long lastMessageSeqId
) {
}
