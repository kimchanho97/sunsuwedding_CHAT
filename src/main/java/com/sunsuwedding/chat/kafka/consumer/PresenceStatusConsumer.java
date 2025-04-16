package com.sunsuwedding.chat.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunsuwedding.chat.client.PresencePushClient;
import com.sunsuwedding.chat.dto.presece.PresenceStatusMessageResponse;
import com.sunsuwedding.chat.event.message.PresenceStatusEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PresenceStatusConsumer {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;
    private final PresencePushClient presencePushClient;

    @Value("${chat.server-id}")
    private String serverId; // 현재 서버의 식별자

    @KafkaListener(topics = "presence-status", groupId = "presence-consumer-group")
    public void listen(String message) {
        try {
            PresenceStatusEvent statusEvent = objectMapper.readValue(message, PresenceStatusEvent.class);
            Long userId = statusEvent.getUserId();
            String targetServer = statusEvent.getServerId();

            PresenceStatusMessageResponse messageResponse = new PresenceStatusMessageResponse(userId, statusEvent.getStatus());
            if (serverId.equals(targetServer)) {
                log.info("✅ WebSocket 전송: userId={}", userId);
                messagingTemplate.convertAndSend("/topic/presence/" + userId, messageResponse);
            } else {
                log.info("📡 다른 서버에 유저 존재 → HTTP 전송: {}", targetServer);
                presencePushClient.sendPresence(targetServer, messageResponse);
            }

        } catch (Exception e) {
            log.error("❌ PresenceStatus Consumer 처리 실패", e);
        }
    }
}
