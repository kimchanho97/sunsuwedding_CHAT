package com.sunsuwedding.chat.dto.room;

import java.util.List;

public record ChatRoomPartnerProfileRequest(
        Long requesterId,
        List<String> chatRoomCodes
) {
}
