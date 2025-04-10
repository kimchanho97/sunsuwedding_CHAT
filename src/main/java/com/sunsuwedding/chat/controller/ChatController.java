package com.sunsuwedding.chat.controller;


import com.sunsuwedding.chat.dto.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat-rooms/{roomId}/messages")
    public void send(@DestinationVariable Long roomId, @Payload ChatMessage message) {
        log.info("Room: {}", roomId);
        messagingTemplate.convertAndSend("/topic/chat/rooms/" + roomId, message);
    }
}
