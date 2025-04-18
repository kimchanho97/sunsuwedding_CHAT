package com.sunsuwedding.chat.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunsuwedding.chat.client.PresencePushClient;
import com.sunsuwedding.chat.dto.presence.PresenceStatusDto;
import com.sunsuwedding.chat.dto.presence.PresenceStatusMessageResponse;
import com.sunsuwedding.chat.event.PresenceStatusEvent;
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

    @Value("${current.server-url}")
    private String currentServerUrl;

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
        PresenceStatusDto status = new PresenceStatusDto(
                event.getUserId(),
                event.getChatRoomCode(),
                event.getStatus()
        );

        if (currentServerUrl.equals(event.getTargetServerUrl())) {
            pushToWebSocket(status);
        } else {
            pushToRemoteServer(event.getTargetServerUrl(), status);
        }
    }

    private void pushToWebSocket(PresenceStatusDto status) {
        PresenceStatusMessageResponse response = new PresenceStatusMessageResponse(
                status.getUserId(),
                status.getStatus()
        );
        messagingTemplate.convertAndSend(
                "/topic/presence/" + status.getChatRoomCode() + "/" + status.getUserId(),
                response
        );
    }

    private void pushToRemoteServer(String serverUrl, PresenceStatusDto status) {
        presencePushClient.sendPresence(serverUrl, status);
    }
}
