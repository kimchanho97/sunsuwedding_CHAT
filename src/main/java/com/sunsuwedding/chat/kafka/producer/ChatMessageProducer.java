package com.sunsuwedding.chat.kafka.producer;

import com.sunsuwedding.chat.event.ChatMessageRequestEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatMessageProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void send(ChatMessageRequestEvent event) {
        kafkaTemplate.send(
                "chat-message",
                event.getChatRoomCode(),
                event
        );
    }
}
