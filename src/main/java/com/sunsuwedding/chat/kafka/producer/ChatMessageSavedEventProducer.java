package com.sunsuwedding.chat.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunsuwedding.chat.event.ChatMessageSavedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatMessageSavedEventProducer {

    private final ObjectMapper objectMapper;

    public void sendTransactional(KafkaOperations<String, String> kafkaOps, ChatMessageSavedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaOps.send("chat-message-saved", event.getChatRoomCode(), payload);
        } catch (JsonProcessingException e) {
            log.error("❌ ChatMessageSavedEvent 직렬화 실패", e);
        }
    }
}
