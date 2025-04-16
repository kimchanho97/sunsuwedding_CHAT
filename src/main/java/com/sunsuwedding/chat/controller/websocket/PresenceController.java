package com.sunsuwedding.chat.controller.websocket;

import com.sunsuwedding.chat.dto.presece.PresenceMessageRequest;
import com.sunsuwedding.chat.dto.presece.PresencePingMessageRequest;
import com.sunsuwedding.chat.service.PresenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PresenceController {

    private final PresenceService presenceService;

    @MessageMapping("/presence")
    public void handlePresence(@Valid @Payload PresenceMessageRequest message,
                               @Header("simpSessionId") String sessionId) {
        presenceService.handleConnect(message.getUserId(), message.getChatPartnerId(), sessionId);
    }

    @MessageMapping("/presence/ping")
    public void handlePing(@Valid @Payload PresencePingMessageRequest message,
                           @Header("simpSessionId") String sessionId) {
        presenceService.handlePing(message.getUserId(), sessionId);
    }

}
