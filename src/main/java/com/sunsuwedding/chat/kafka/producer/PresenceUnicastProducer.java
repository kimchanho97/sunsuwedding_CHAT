package com.sunsuwedding.chat.kafka.producer;

import com.sunsuwedding.chat.event.PresenceStatusEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PresenceUnicastProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void send(PresenceStatusEvent event) {
        kafkaTemplate.send(
                "presence-status",
                event.getChatRoomCode(),
                event
        );
    }
}
