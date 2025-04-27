package com.sunsuwedding.chat.controller.internal;

import com.sunsuwedding.chat.common.util.WebSocketUtils;
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
        String logContext = String.format("[Message][%s][%s]", chatRoomCode, message.getSequenceId());

        WebSocketUtils.sendMessage(
                messagingTemplate,
                destination,
                message,
                logContext
        );
        // 웹소켓 전송 결과와 관계없이 HTTP 응답은 항상 성공(200)으로 반환
        return ResponseEntity.ok().build();
    }
}
