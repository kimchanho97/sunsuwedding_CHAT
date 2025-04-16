package com.sunsuwedding.chat.controller.api;

import com.sunsuwedding.chat.dto.presece.PresenceStatusMessageResponse;
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
    public ResponseEntity<Void> pushPresence(@RequestBody PresenceStatusMessageResponse status) {
        messagingTemplate.convertAndSend("/topic/presence/" + status.getUserId(), status);
        return ResponseEntity.ok().build();
    }
}
