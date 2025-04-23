package com.sunsuwedding.chat.dto.message;

import com.sunsuwedding.chat.event.ChatMessageSavedEvent;
import com.sunsuwedding.chat.model.ChatMessageDocument;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {

    private String messageId;
    private Long senderId;
    private String senderName;
    private String content;
    private String messageType;
    private LocalDateTime createdAt;
    private Long sequenceId;
    private List<Long> readBy;

    // 이미지 메시지인 경우
    private String fileUrl;

    public static ChatMessageResponse from(ChatMessageDocument doc, Map<Long, Long> userReadSeqMap) {
        List<Long> readBy = userReadSeqMap.entrySet().stream()
                .filter(entry -> entry.getValue() >= doc.getMessageSeqId())
                .map(Map.Entry::getKey)
                .toList();

        LocalDateTime createdAtKST = doc.getCreatedAt()
                .atZone(ZoneId.of("UTC"))                     // 1. 현재 UTC 기준으로 ZonedDateTime 생성
                .withZoneSameInstant(ZoneId.of("Asia/Seoul")) // 2. KST로 타임존 변경
                .toLocalDateTime();                                  // 3. 다시 LocalDateTime으로 추출

        return new ChatMessageResponse(
                doc.getId(),
                doc.getSenderId(),
                doc.getSenderName(),
                doc.getContent(),
                doc.getMessageType(),
                createdAtKST,
                doc.getMessageSeqId(),
                readBy,
                doc.getFileUrl()
        );
    }

    public static ChatMessageResponse from(ChatMessageSavedEvent event, List<Long> onlineUserIds) {
        LocalDateTime createdAtKST = event.getCreatedAt()
                .atZone(ZoneId.of("UTC"))                     // 1. 현재 UTC 기준으로 ZonedDateTime 생성
                .withZoneSameInstant(ZoneId.of("Asia/Seoul")) // 2. KST로 타임존 변경
                .toLocalDateTime();                                  // 3. 다시 LocalDateTime으로 추출

        return new ChatMessageResponse(
                event.getMessageId(),
                event.getSenderId(),
                event.getSenderName(),
                event.getContent(),
                event.getMessageType(),
                createdAtKST,
                event.getSequenceId(),
                onlineUserIds,
                event.getFileUrl()
        );
    }

}
