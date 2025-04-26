package com.sunsuwedding.chat.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunsuwedding.chat.event.ChatMessageReadSyncBatchEvent;
import com.sunsuwedding.chat.redis.RedisChatReadStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatMessageReadSyncBatchConsumer {

    private final ObjectMapper objectMapper;
    private final RedisChatReadStore redisChatReadStore;

    @KafkaListener(topics = "chat-message-read-sync", groupId = "chat-read-seq-group")
    public void consume(String payload, Acknowledgment ack) {
        try {
            ChatMessageReadSyncBatchEvent event = objectMapper.readValue(payload, ChatMessageReadSyncBatchEvent.class);
            event.userIds().forEach(userId -> {
                // 1. Redis에 읽음 시퀀스 저장
                redisChatReadStore.markMessageAsRead(event.chatRoomCode(), userId, event.messageSequenceId());

                // 2. Redis에 읽음 시퀀스 dirty 저장소에 키 추가
                redisChatReadStore.markLastReadSequenceAsDirty(event.chatRoomCode(), userId);
            });
            ack.acknowledge();
        } catch (Exception e) {
            log.error("❌ chat-message-read-sync 후속 이벤트 처리 실패", e);
        }
    }
}
