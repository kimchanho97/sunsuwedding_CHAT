package com.sunsuwedding.chat.controller.websocket;


import com.sunsuwedding.chat.dto.message.ChatMessageRequest;
import com.sunsuwedding.chat.event.ChatMessageRequestEvent;
import com.sunsuwedding.chat.kafka.producer.ChatMessageProducer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatMessageProducer chatMessageProducer;

    @MessageMapping("/chat/rooms/{chatRoomCode}/messages")
    public void send(@DestinationVariable String chatRoomCode, @Payload @Valid ChatMessageRequest message) {
        chatMessageProducer.send(ChatMessageRequestEvent.from(message, chatRoomCode));
    }
}
