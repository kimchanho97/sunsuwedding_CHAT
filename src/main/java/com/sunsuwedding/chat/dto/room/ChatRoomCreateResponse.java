package com.sunsuwedding.chat.dto.room;

public record ChatRoomCreateResponse(
        String chatRoomCode,
        boolean alreadyExists
) {
}
