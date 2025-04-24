package com.sunsuwedding.chat.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDateTime;

public record ChatRoomMeta(
        String lastMessage,

        @JsonSerialize(using = LoggingLocalDateTimeSerializer.class)
        LocalDateTime lastMessageAt,
        Long lastMessageSeqId
) {
}
