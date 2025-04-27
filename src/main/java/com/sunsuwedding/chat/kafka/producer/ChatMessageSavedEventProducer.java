package com.sunsuwedding.chat.kafka.producer;

import com.sunsuwedding.chat.event.ChatMessageSavedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ChatMessageSavedEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ChatMessageSavedEventProducer(
            @Qualifier("transactionalKafkaTemplate") KafkaTemplate<String, Object> kafkaTemplate
    ) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTransactional(ChatMessageSavedEvent event) {
        kafkaTemplate.send(
                "chat-message-saved",
                event.getChatRoomCode(),
                event
        );
    }
}
