package com.sunsuwedding.chat.event;

import java.time.ZoneOffset;

public record ChatRoomResortEvent(
        Long userId,
        String chatRoomCode,
        Long lastMessageAtMillis
) {
    public static ChatRoomResortEvent from(ChatMessageSavedEvent event, Long userId) {
        return new ChatRoomResortEvent(userId, event.getChatRoomCode(), event.getCreatedAt().toInstant(ZoneOffset.UTC).toEpochMilli());
    }
}
