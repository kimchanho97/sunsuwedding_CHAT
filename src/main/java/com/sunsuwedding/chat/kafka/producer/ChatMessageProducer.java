package com.sunsuwedding.chat.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunsuwedding.chat.dto.message.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatMessageProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void send(ChatMessage message) {
        try {
            String payload = objectMapper.writeValueAsString(message);

            log.info("[Kafka Producer] 메시지 전송: {}", payload);
            kafkaTemplate.send("chat-message", payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Kafka 메시지 직렬화 실패", e);
        }
    }
}
