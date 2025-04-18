package com.sunsuwedding.chat.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunsuwedding.chat.event.PresenceStatusEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PresenceUnicastProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void send(PresenceStatusEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("presence-status", String.valueOf(event.getUserId()), payload);
            // TODO: 추가적으로 전송 실패시 콜백 처리도 가능
        } catch (JsonProcessingException e) {
            log.error("❌ Kafka 직렬화 실패 - 이벤트 구조 확인 필요: {}", event, e);
        }
    }
}
