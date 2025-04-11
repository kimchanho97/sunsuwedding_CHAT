package com.sunsuwedding.chat.dto;

import com.sunsuwedding.chat.domain.ChatMessageDocument;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class ChatMessageListResponse {
    private List<ChatMessageResponse> data;
    private boolean hasNext;

    public static ChatMessageListResponse from(Slice<ChatMessageDocument> slice) {
        List<ChatMessageResponse> messages = slice.getContent().stream()
                .map(ChatMessageResponse::from)
                .toList();

        return ChatMessageListResponse.of(messages, slice.hasNext());
    }
}
