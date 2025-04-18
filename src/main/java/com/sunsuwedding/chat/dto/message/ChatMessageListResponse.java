package com.sunsuwedding.chat.dto.message;

import com.sunsuwedding.chat.domain.ChatMessageDocument;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageListResponse {

    private List<ChatMessageResponse> data;
    private boolean hasNext;

    public static ChatMessageListResponse from(Slice<ChatMessageDocument> slice, Map<Long, Long> userReadSeqMap) {
        List<ChatMessageResponse> messages = slice.getContent().stream()
                .map(doc -> ChatMessageResponse.from(doc, userReadSeqMap))
                .toList();

        return new ChatMessageListResponse(messages, slice.hasNext());
    }
}
