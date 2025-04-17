package com.sunsuwedding.chat.event.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageSavedEvent {
    private String chatRoomCode;
    private Long senderId;
    private String senderName;
    private String content;
    private String messageType;
    private LocalDateTime createdAt;
    private Long sequenceId;
    private List<Long> readBy; // 초기엔 senderId만 포함
}
