package com.sunsuwedding.chat.kafka.producer;

import com.sunsuwedding.chat.event.ChatMessageReadSyncBatchEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatMessageReadSyncBatchEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void send(ChatMessageReadSyncBatchEvent event) {
        kafkaTemplate.send(
                "chat-message-read-sync",
                event.chatRoomCode(),
                event
        );
    }
}
