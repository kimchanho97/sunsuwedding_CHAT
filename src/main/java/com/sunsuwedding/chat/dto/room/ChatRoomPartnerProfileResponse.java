package com.sunsuwedding.chat.dto.room;

public record ChatRoomPartnerProfileResponse(
        String chatRoomCode,
        Long partnerUserId,
        String partnerName,
        String avatarUrl
) {
}