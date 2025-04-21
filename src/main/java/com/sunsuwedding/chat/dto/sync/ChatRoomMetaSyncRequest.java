package com.sunsuwedding.chat.dto.sync;

public record ChatRoomMetaSyncRequest(
        String chatRoomCode,
        String lastMessage,
        String lastMessageAt,
        String lastMessageSeqId
) {
}