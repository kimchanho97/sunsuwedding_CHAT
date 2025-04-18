package com.sunsuwedding.chat.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunsuwedding.chat.client.interserver.PresenceInterServerClient;
import com.sunsuwedding.chat.dto.presence.PresenceStatusDto;
import com.sunsuwedding.chat.dto.presence.PresenceStatusMessageResponse;
import com.sunsuwedding.chat.event.PresenceStatusEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PresenceUnicastConsumer {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;
    private final PresenceInterServerClient presenceInterServerClient;

    @Value("${current.server-url}")
    private String currentServerUrl;

    @KafkaListener(topics = "presence-status", groupId = "presence-unicast-group")
    public void consume(String payload, Acknowledgment ack) {
        try {
            PresenceStatusEvent event = objectMapper.readValue(payload, PresenceStatusEvent.class);
            handleUnicast(event);
            ack.acknowledge();
        } catch (JsonProcessingException e) {
            log.error("❌ PresenceStatusEvent 역직렬화 실패: {}", payload, e);
        } catch (Exception e) {
            log.error("❌ PresenceStatusEvent 처리 실패", e);
        }
    }

    private void handleUnicast(PresenceStatusEvent event) {
        PresenceStatusDto message = new PresenceStatusDto(
                event.getUserId(),
                event.getChatRoomCode(),
                event.getStatus()
        );

        if (currentServerUrl.equals(event.getTargetServerUrl())) {
            sendToWebSocket(message);
        } else {
            sendToRemoteServer(event.getTargetServerUrl(), message);
        }
    }

    private void sendToWebSocket(PresenceStatusDto message) {
        PresenceStatusMessageResponse response = new PresenceStatusMessageResponse(
                message.getUserId(),
                message.getStatus()
        );
        String destination = "/topic/presence/" + message.getChatRoomCode() + "/" + message.getUserId();
        messagingTemplate.convertAndSend(destination, response);
    }

    private void sendToRemoteServer(String targetServerUrl, PresenceStatusDto message) {
        presenceInterServerClient.sendPresence(targetServerUrl, message);
    }
}
