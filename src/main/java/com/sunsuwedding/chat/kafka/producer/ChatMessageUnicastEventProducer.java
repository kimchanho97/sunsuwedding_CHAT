package com.sunsuwedding.chat.kafka.producer;

import com.sunsuwedding.chat.event.ChatMessageUnicastEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatMessageUnicastEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void send(ChatMessageUnicastEvent event) {
        kafkaTemplate.send(
                "chat-message-unicast",
                event.chatRoomCode(),
                event
        );
    }
}
