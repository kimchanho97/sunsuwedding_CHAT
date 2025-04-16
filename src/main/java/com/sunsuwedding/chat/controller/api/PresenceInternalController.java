package com.sunsuwedding.chat.controller.api;

import com.sunsuwedding.chat.dto.presece.PresenceStatusMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/presence")
public class PresenceInternalController {

    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/push")
    public ResponseEntity<Void> pushPresence(@RequestBody PresenceStatusMessage status) {
        log.info("📨 내부 Presence 푸시 수신: userId={}, status={}", status.getUserId(), status.getStatus());

        // 실제 WebSocket 전송
        messagingTemplate.convertAndSend("/topic/presence/" + status.getUserId(), status);
        return ResponseEntity.ok().build();
    }
}
