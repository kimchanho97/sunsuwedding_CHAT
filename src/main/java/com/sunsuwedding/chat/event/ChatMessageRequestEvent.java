package com.sunsuwedding.chat.event;

import com.sunsuwedding.chat.domain.ChatMessageDocument;
import com.sunsuwedding.chat.dto.message.ChatMessageRequest;
import com.sunsuwedding.chat.dto.message.S3UploadResultDto;
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

    // 이미지 메시지인 경우
    private String fileName;
    private String fileUrl;

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

    public static ChatMessageRequestEvent from(ChatMessageRequest request, String chatRoomCode, S3UploadResultDto uploadResult) {
        return ChatMessageRequestEvent.builder()
                .chatRoomCode(chatRoomCode)
                .senderId(request.getSenderId())
                .senderName(request.getSenderName())
                .content(request.getContent())
                .messageType(request.getMessageType())
                .createdAt(request.getCreatedAt())
                .fileName(uploadResult.getFileName())
                .fileUrl(uploadResult.getFileUrl())
                .build();
    }

    public ChatMessageDocument toDocument(Long seqId) {
        return ChatMessageDocument.builder()
                .chatRoomCode(this.chatRoomCode)
                .senderId(this.senderId)
                .senderName(this.senderName)
                .content(this.content)
                .messageType(this.messageType)
                .createdAt(LocalDateTime.now())
                .messageSeqId(seqId)
                .fileName(this.fileName)
                .fileUrl(this.fileUrl)
                .build();
    }
}
