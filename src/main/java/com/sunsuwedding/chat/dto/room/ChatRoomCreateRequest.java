package com.sunsuwedding.chat.dto.room;

import jakarta.validation.constraints.NotNull;

public record ChatRoomCreateRequest(

        @NotNull(message = "채팅방 ID는 필수입니다.")
        Long userId,
        @NotNull(message = "플래너 ID는 필수입니다.")
        Long plannerId
) {
}