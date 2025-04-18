package com.sunsuwedding.chat.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunsuwedding.chat.event.ChatRoomResortEvent;
import com.sunsuwedding.chat.redis.RedisChatRoomStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatRoomResortConsumer {

    private final ObjectMapper objectMapper;
    private final RedisChatRoomStore redisChatRoomStore;

    @KafkaListener(topics = "chat-room-resort", groupId = "chat-room-resort-group")
    public void consume(String payload) {
        try {
            ChatRoomResortEvent event = objectMapper.readValue(payload, ChatRoomResortEvent.class);
            redisChatRoomStore.resortChatRoom(event.userId(), event.chatRoomCode(), event.lastMessageAtMillis());
        } catch (Exception e) {
            log.warn("⚠️ 채팅방 목록 정렬 실패", e);
        }
    }
}
