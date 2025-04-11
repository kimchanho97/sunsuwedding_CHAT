package com.sunsuwedding.chat.dto.room;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomValidationRequest {

    @NotNull(message = "채팅방 ID는 필수입니다.")
    private Long chatRoomId;
    @NotNull(message = "사용자 ID는 필수입니다.")
    private Long userId;
}