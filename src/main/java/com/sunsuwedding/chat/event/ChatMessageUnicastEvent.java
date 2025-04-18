package com.sunsuwedding.chat.event;

import com.sunsuwedding.chat.dto.message.ChatMessageResponse;

import java.util.List;

public record ChatMessageUnicastEvent(
        String chatRoomCode,
        String targetServerUrl,
        ChatMessageResponse response
) {
    public static ChatMessageUnicastEvent from(
            ChatMessageSavedEvent savedEvent,
            String serverUrl,
            List<Long> onlineUserIds
    ) {
        ChatMessageResponse response = ChatMessageResponse.from(savedEvent, onlineUserIds);
        return new ChatMessageUnicastEvent(savedEvent.getChatRoomCode(), serverUrl, response);
    }
}
