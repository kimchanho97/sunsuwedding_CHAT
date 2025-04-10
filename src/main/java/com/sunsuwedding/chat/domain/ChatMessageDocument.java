package com.sunsuwedding.chat.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "chat_messages")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDocument {

    @Id
    private String id;

    private Long chatRoomId;
    private Long senderId;
    private String senderName;
    private String content;
    private String messageType; // TEXT, IMAGE
    private LocalDateTime createdAt;
}
