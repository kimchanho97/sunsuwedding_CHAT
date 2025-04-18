package com.sunsuwedding.chat.event;

import com.sunsuwedding.chat.domain.ChatMessageDocument;
import com.sunsuwedding.chat.dto.message.ChatMessageRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
public class ChatMessageRequestEvent {
    private String chatRoomCode;
    private Long senderId;
    private String senderName;
    private String content;
    private String messageType;
    private LocalDateTime createdAt;

    public static ChatMessageRequestEvent from(ChatMessageRequest request, String chatRoomCode) {
        return ChatMessageRequestEvent.builder()
                .chatRoomCode(chatRoomCode)
                .senderId(request.getSenderId())
                .senderName(request.getSenderName())
                .content(request.getContent())
                .messageType(request.getMessageType())
                .createdAt(request.getCreatedAt())
                .build();
    }

    public ChatMessageDocument toDocument(Long seqId) {
        return ChatMessageDocument.builder()
                .chatRoomCode(this.chatRoomCode)
                .senderId(this.senderId)
                .senderName(this.senderName)
                .content(this.content)
                .messageType(this.messageType)
                .createdAt(this.createdAt)
                .messageSeqId(seqId)
                .build();
    }
}
