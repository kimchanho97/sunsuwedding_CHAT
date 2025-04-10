package com.sunsuwedding.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    private Long chatRoomId;
    private Long senderId;
    private String senderName;
    private String content;
    private String messageType; // "TEXT" or "IMAGE"
    private LocalDateTime createdAt;
}
