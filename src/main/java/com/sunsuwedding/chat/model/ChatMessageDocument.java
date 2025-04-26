package com.sunsuwedding.chat.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "chat_messages")
@CompoundIndex(
        name = "chatRoomCode_messageSeqId_idx",
        def = "{'chatRoomCode' : 1, 'messageSeqId' : 1}",
        unique = true) // 복합 인덱스 설정 -> 쿼리 성능 향상 + unique 제약 조건 설정
public class ChatMessageDocument {

    @Id
    private String id;
    private String chatRoomCode; // 채팅방 CODE - UUID
    private Long senderId;
    private String senderName;
    private String content;
    private String messageType; // TEXT, IMAGE
    private LocalDateTime createdAt;
    private Long messageSeqId; // 메시지 시퀀스 ID

    // 이미지 메시지인 경우
    private String fileName;
    private String fileUrl;
}
