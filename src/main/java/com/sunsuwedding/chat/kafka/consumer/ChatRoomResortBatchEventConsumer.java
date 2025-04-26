package com.sunsuwedding.chat.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunsuwedding.chat.event.ChatRoomResortBatchEvent;
import com.sunsuwedding.chat.redis.RedisChatRoomStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatRoomResortBatchEventConsumer {

    private final ObjectMapper objectMapper;
    private final RedisChatRoomStore redisChatRoomStore;

    @KafkaListener(topics = "chat-room-resort", groupId = "chat-room-resort-group")
    public void consume(String payload, Acknowledgment ack) {
        try {
            ChatRoomResortBatchEvent event = objectMapper.readValue(payload, ChatRoomResortBatchEvent.class);
            event.userIds().forEach(userId ->
                    redisChatRoomStore.resortChatRoom(userId, event.chatRoomCode(), event.lastMessageAtMillis())
            );
            ack.acknowledge();
        } catch (Exception e) {
            log.error("❌ chat-room-resort 후속 이벤트 처리 실패", e);
        }
    }

}
