package com.sunsuwedding.chat.dto.message;

import com.sunsuwedding.chat.event.message.ChatMessageSavedEvent;
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
public class ChatMessageUnicastDto {
    private String chatRoomCode;
    private Long senderId;
    private String senderName;
    private String content;
    private String messageType;
    private LocalDateTime createdAt;
    private Long sequenceId;
    private List<Long> readBy;

    public static ChatMessageUnicastDto from(ChatMessageSavedEvent event, List<Long> readBy) {
        return ChatMessageUnicastDto.builder()
                .chatRoomCode(event.getChatRoomCode())
                .senderId(event.getSenderId())
                .senderName(event.getSenderName())
                .content(event.getContent())
                .messageType(event.getMessageType())
                .createdAt(event.getCreatedAt())
                .sequenceId(event.getSequenceId())
                .readBy(readBy)
                .build();
    }

}
