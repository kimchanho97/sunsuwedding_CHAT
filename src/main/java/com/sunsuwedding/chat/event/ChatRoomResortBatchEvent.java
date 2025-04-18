package com.sunsuwedding.chat.event;

import java.time.ZoneOffset;
import java.util.List;

public record ChatRoomResortBatchEvent(
        List<Long> userIds,
        String chatRoomCode,
        Long lastMessageAtMillis
) {
    public static ChatRoomResortBatchEvent from(ChatMessageSavedEvent event, List<Long> userIds) {
        return new ChatRoomResortBatchEvent(
                userIds,
                event.getChatRoomCode(),
                event.getCreatedAt().toInstant(ZoneOffset.UTC).toEpochMilli()
        );
    }
}
