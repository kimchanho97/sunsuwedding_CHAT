package com.sunsuwedding.chat.dto.message;

import com.sunsuwedding.chat.domain.ChatMessageDocument;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class ChatMessageResponse {

    private String id;
    private Long senderId;
    private String senderName;
    private String content;
    private String messageType;
    private LocalDateTime createdAt;

    public static ChatMessageResponse from(ChatMessageDocument doc) {
        return ChatMessageResponse.of(
                doc.getId(), doc.getSenderId(), doc.getSenderName(),
                doc.getContent(), doc.getMessageType(), doc.getCreatedAt()
        );
    }
}
