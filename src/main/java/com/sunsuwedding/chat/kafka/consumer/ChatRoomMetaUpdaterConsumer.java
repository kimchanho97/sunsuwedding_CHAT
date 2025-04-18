package com.sunsuwedding.chat.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunsuwedding.chat.event.ChatMessageSavedEvent;
import com.sunsuwedding.chat.redis.RedisChatRoomStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatRoomMetaUpdaterConsumer {

    private final ObjectMapper objectMapper;
    private final RedisChatRoomStore redisChatRoomStore;

    @KafkaListener(topics = "chat-message-saved", groupId = "chat-room-meta-group")
    public void consume(String payload, Acknowledgment ack) {
        try {
            ChatMessageSavedEvent event = objectMapper.readValue(payload, ChatMessageSavedEvent.class);
            redisChatRoomStore.updateChatRoomMeta(
                    event.getChatRoomCode(),
                    event.getContent(),
                    event.getCreatedAt(),
                    event.getSequenceId()
            );
            ack.acknowledge();
        } catch (Exception e) {
            log.warn("⚠️ Redis 메타 정보 갱신 실패: {}", e.getMessage());
            // TODO: MongoDB 기반으로 chat:room:meta:* rebuild 배치 구현(Eventually Consistent 보장)
        }
    }
}
