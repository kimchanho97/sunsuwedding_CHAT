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
            // 1. 메타 정보 업데이트
            redisChatRoomStore.updateChatRoomMeta(
                    event.getChatRoomCode(),
                    event.getContent(),
                    event.getCreatedAt(),
                    event.getSequenceId()
            );

            // 2. dirty 저장소에 키 추가
            redisChatRoomStore.markChatRoomMetaAsDirty(event.getChatRoomCode());
            ack.acknowledge();
        } catch (Exception e) {
            log.error("❌ chat-room-meta 업데이트 실패", e);
        }
    }
}
