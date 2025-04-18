package com.sunsuwedding.chat.dto.message;

import com.sunsuwedding.chat.domain.ChatMessageDocument;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {

    private Long senderId;
    private String senderName;
    private String content;
    private String messageType;
    private LocalDateTime createdAt;
    private Long sequenceId;
    private List<Long> readBy;

    public static ChatMessageResponse from(ChatMessageDocument doc, Map<Long, Long> userReadSeqMap) {
        List<Long> readBy = userReadSeqMap.entrySet().stream()
                .filter(entry -> entry.getValue() >= doc.getMessageSeqId())
                .map(Map.Entry::getKey)
                .toList();

        return new ChatMessageResponse(
                doc.getSenderId(),
                doc.getSenderName(),
                doc.getContent(),
                doc.getMessageType(),
                doc.getCreatedAt(),
                doc.getMessageSeqId(),
                readBy
        );
    }
}
