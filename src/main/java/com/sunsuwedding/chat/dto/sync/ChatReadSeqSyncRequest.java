package com.sunsuwedding.chat.dto.sync;

public record ChatReadSeqSyncRequest(
        String chatRoomCode,
        Long userId,
        Long lastReadSeqId
) {
}