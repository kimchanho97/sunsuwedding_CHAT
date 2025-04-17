package com.sunsuwedding.chat.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
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
    private String currentServerId;

    @KafkaListener(topics = "presence-status", groupId = "presence-consumer-group")
    public void consume(String rawMessage) {
        try {
            PresenceStatusEvent event = objectMapper.readValue(rawMessage, PresenceStatusEvent.class);
            handleEvent(event);
        } catch (JsonProcessingException e) {
            log.error("❌ PresenceStatusEvent 직렬화 실패: {}", rawMessage, e);
        } catch (Exception e) {
            log.error("❌ PresenceStatusEvent 처리 실패", e);
        }
    }

    private void handleEvent(PresenceStatusEvent event) {
        Long userId = event.getUserId();
        String targetServerId = event.getTargetServerId();

        PresenceStatusMessageResponse response = new PresenceStatusMessageResponse(userId, event.getStatus());

        if (currentServerId.equals(targetServerId)) {
            pushToWebSocket(userId, response);
        } else {
            pushToRemoteServer(targetServerId, response);
        }
    }

    private void pushToWebSocket(Long userId, PresenceStatusMessageResponse response) {
        messagingTemplate.convertAndSend("/topic/presence/" + userId, response);
    }

    private void pushToRemoteServer(String serverId, PresenceStatusMessageResponse response) {
        presencePushClient.sendPresence(serverId, response);
    }
}
