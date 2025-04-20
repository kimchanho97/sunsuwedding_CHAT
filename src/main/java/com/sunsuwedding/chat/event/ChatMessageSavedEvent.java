package com.sunsuwedding.chat.event;

import com.sunsuwedding.chat.model.ChatMessageDocument;
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

    private String messageId;
    private String chatRoomCode;
    private Long senderId;
    private String senderName;
    private String content;
    private String messageType;
    private LocalDateTime createdAt;
    private Long sequenceId;

    // 이미지 메시지인 경우
    private String fileUrl;

    public static ChatMessageSavedEvent from(ChatMessageDocument document) {
        return ChatMessageSavedEvent.builder()
                .messageId(document.getId())
                .chatRoomCode(document.getChatRoomCode())
                .senderId(document.getSenderId())
                .senderName(document.getSenderName())
                .content(document.getContent())
                .messageType(document.getMessageType())
                .createdAt(document.getCreatedAt())
                .sequenceId(document.getMessageSeqId())
                .fileUrl(document.getFileUrl())
                .build();
    }
}
