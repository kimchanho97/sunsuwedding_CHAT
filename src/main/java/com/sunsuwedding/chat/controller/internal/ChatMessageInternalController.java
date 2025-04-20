package com.sunsuwedding.chat.controller.internal;

import com.sunsuwedding.chat.dto.message.ChatMessageResponse;
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

    @PostMapping("/unicast/{chatRoomCode}")
    public ResponseEntity<Void> broadcastToChatRoom(@PathVariable String chatRoomCode,
                                                    @RequestBody ChatMessageResponse message) {
        String destination = "/topic/chat/rooms/" + chatRoomCode;
        messagingTemplate.convertAndSend(destination, message);
        return ResponseEntity.ok().build();
    }
}
