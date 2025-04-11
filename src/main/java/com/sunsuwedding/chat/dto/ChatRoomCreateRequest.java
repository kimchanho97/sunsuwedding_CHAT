package com.sunsuwedding.chat.dto;

import jakarta.validation.constraints.NotNull;

public record ChatRoomCreateRequest(
        @NotNull Long userId,
        @NotNull Long plannerId
) {
}