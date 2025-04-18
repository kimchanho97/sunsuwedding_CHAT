package com.sunsuwedding.chat.controller.api.internal;

import com.sunsuwedding.chat.dto.message.ChatMessageUnicastDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/chat/messages")
public class ChatMessageInternalController {

    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/unicast/{userId}")
    public ResponseEntity<Void> unicastMessage(@PathVariable Long userId, @RequestBody ChatMessageUnicastDto message) {
        String destination = "/topic/chat-rooms/" + message.getChatRoomCode() + "/" + userId;
        messagingTemplate.convertAndSend(destination, message);
        return ResponseEntity.ok().build();
    }
}

