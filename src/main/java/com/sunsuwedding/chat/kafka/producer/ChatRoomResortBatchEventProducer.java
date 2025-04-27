package com.sunsuwedding.chat.kafka.producer;

import com.sunsuwedding.chat.event.ChatRoomResortBatchEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatRoomResortBatchEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void send(ChatRoomResortBatchEvent event) {
        kafkaTemplate.send(
                "chat-room-resort",
                event.chatRoomCode(),
                event
        );
    }
}
