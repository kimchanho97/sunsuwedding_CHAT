package com.sunsuwedding.chat.dto.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    @NotNull(message = "채팅방 ID는 필수입니다.")
    private String chatRoomId;

    @NotNull(message = "보내는 사람 ID는 필수입니다.")
    private Long senderId;

    @NotBlank(message = "보내는 사람 이름은 비어 있을 수 없습니다.")
    private String senderName;

    @NotBlank(message = "메시지 내용은 비어 있을 수 없습니다.")
    @Size(max = 1000, message = "메시지 내용은 최대 1000자까지 가능합니다.")
    private String content;

    @NotBlank(message = "메시지 타입은 필수입니다.")
    @Pattern(regexp = "TEXT|IMAGE", message = "메시지 타입은 TEXT 또는 IMAGE여야 합니다.")
    private String messageType;

    @NotNull(message = "생성 시간은 필수입니다.")
    private LocalDateTime createdAt;
}
