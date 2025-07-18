package com.sunsuwedding.chat.event;

import com.sunsuwedding.chat.dto.message.ChatMessageRequest;
import com.sunsuwedding.chat.dto.message.S3UploadResultDto;
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
public class ChatMessageRequestEvent {
    private String chatRoomCode;
    private String messageId;
    private Long senderId;
    private String senderName;
    private String content;
    private String messageType;
    private LocalDateTime createdAt;
    private Long messageSeqId;

    // 이미지 메시지인 경우
    private String fileName;
    private String fileUrl;

    public static ChatMessageRequestEvent from(ChatMessageRequest request, String chatRoomCode, Long messageSeqId) {
        return ChatMessageRequestEvent.builder()
                .chatRoomCode(chatRoomCode)
                .messageId(request.getMessageId())
                .senderId(request.getSenderId())
                .senderName(request.getSenderName())
                .content(request.getContent())
                .messageType(request.getMessageType())
                .createdAt(request.getCreatedAt())
                .messageSeqId(messageSeqId)
                .build();
    }

    public static ChatMessageRequestEvent from(ChatMessageRequest request, String chatRoomCode, S3UploadResultDto uploadResult, Long messageSeqId) {
        return ChatMessageRequestEvent.builder()
                .chatRoomCode(chatRoomCode)
                .messageId(request.getMessageId())
                .senderId(request.getSenderId())
                .senderName(request.getSenderName())
                .content(request.getContent())
                .messageType(request.getMessageType())
                .createdAt(request.getCreatedAt())
                .messageSeqId(messageSeqId)
                .fileName(uploadResult.getFileName())
                .fileUrl(uploadResult.getFileUrl())
                .build();
    }

    public ChatMessageDocument toDocument() {
        return ChatMessageDocument.builder()
                .id(this.messageId)
                .chatRoomCode(this.chatRoomCode)
                .senderId(this.senderId)
                .senderName(this.senderName)
                .content(this.content)
                .messageType(this.messageType)
                .createdAt(LocalDateTime.now())
                .messageSeqId(this.messageSeqId)
                .fileName(this.fileName)
                .fileUrl(this.fileUrl)
                .build();
    }
}
