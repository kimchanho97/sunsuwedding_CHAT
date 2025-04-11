package com.sunsuwedding.chat.controller;


import com.sunsuwedding.chat.dto.ChatMessage;
import com.sunsuwedding.chat.kafka.producer.ChatMessageProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageProducer chatMessageProducer;

    @MessageMapping("/chat-rooms/{roomId}/messages")
    public void send(@DestinationVariable Long roomId, @Payload ChatMessage message) {
        log.info("🟢 수신 메시지: {}", message);

        // Kafka로 메시지 전송
        chatMessageProducer.send(message);

        // WebSocket으로 메시지 전송
        messagingTemplate.convertAndSend("/topic/chat/rooms/" + roomId, message);
    }
}
