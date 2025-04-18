package com.sunsuwedding.chat.controller.internal;

import com.sunsuwedding.chat.dto.presence.PresenceStatusDto;
import com.sunsuwedding.chat.dto.presence.PresenceStatusMessageResponse;
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
    public ResponseEntity<Void> pushPresence(@RequestBody PresenceStatusDto status) {
        PresenceStatusMessageResponse response = new PresenceStatusMessageResponse(
                status.getUserId(),
                status.getStatus()
        );
        messagingTemplate.convertAndSend(
                "/topic/presence/" + status.getChatRoomCode() + "/" + status.getUserId(),
                response
        );
        return ResponseEntity.ok().build();
    }
}
