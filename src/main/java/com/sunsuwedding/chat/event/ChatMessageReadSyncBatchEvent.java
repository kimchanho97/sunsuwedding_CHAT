package com.sunsuwedding.chat.event;

import java.util.List;

public record ChatMessageReadSyncBatchEvent(
        String chatRoomCode,
        Long messageSequenceId,
        List<Long> userIds // online user
) {
    public static ChatMessageReadSyncBatchEvent from(ChatMessageSavedEvent event, List<Long> userIds) {
        return new ChatMessageReadSyncBatchEvent(
                event.getChatRoomCode(),
                event.getSequenceId(),
                userIds
        );
    }
}
