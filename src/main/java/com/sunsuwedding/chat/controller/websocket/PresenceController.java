package com.sunsuwedding.chat.controller.websocket;

import com.sunsuwedding.chat.dto.presence.PresenceMessageRequest;
import com.sunsuwedding.chat.dto.presence.PresencePingMessageRequest;
import com.sunsuwedding.chat.service.PresenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PresenceController {

    private final PresenceService presenceService;

    @MessageMapping("/presence/{chatRoomCode}")
    public void handlePresence(@DestinationVariable String chatRoomCode,
                               @Valid @Payload PresenceMessageRequest message,
                               @Header("simpSessionId") String sessionId) {
        presenceService.handleConnect(message.getUserId(), message.getChatPartnerId(), chatRoomCode, sessionId);
    }

    @MessageMapping("/presence/ping/{chatRoomCode}")
    public void handlePing(@DestinationVariable String chatRoomCode,
                           @Valid @Payload PresencePingMessageRequest message,
                           @Header("simpSessionId") String sessionId) {
        presenceService.handlePing(message.getUserId(), chatRoomCode, sessionId);
    }

}
