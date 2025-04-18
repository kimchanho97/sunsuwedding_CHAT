package com.sunsuwedding.chat.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    public static ChatMessageSavedEvent from(ChatMessageRequestEvent request, Long seqId) {
        return ChatMessageSavedEvent.builder()
                .chatRoomCode(request.getChatRoomCode())
                .senderId(request.getSenderId())
                .senderName(request.getSenderName())
                .content(request.getContent())
                .messageType(request.getMessageType())
                .createdAt(request.getCreatedAt())
                .sequenceId(seqId)
                .build();
    }
}
