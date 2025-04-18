package com.sunsuwedding.chat.kafka.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunsuwedding.chat.event.ChatMessageReadSyncBatchEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatMessageReadSyncBatchEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void send(ChatMessageReadSyncBatchEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("chat-message-read-sync", event.chatRoomCode(), payload);
        } catch (Exception e) {
            log.error("❌ ChatMessageReadSyncEvent 전송 실패", e);
        }
    }
}
