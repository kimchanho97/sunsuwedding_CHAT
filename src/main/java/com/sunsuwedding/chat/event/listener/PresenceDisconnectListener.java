package com.sunsuwedding.chat.event.listener;

import com.sunsuwedding.chat.service.PresenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class PresenceDisconnectListener implements ApplicationListener<SessionDisconnectEvent> {

    private final PresenceService presenceService;

    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        String sessionId = StompHeaderAccessor.wrap(event.getMessage()).getSessionId();
        if (sessionId == null) {
            return;
        }

        presenceService.handleDisconnect(sessionId);
    }
}
