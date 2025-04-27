package com.sunsuwedding.chat.kafka.consumer;

import com.sunsuwedding.chat.client.interserver.PresenceInterServerClient;
import com.sunsuwedding.chat.common.util.WebSocketUtils;
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
    private final PresenceInterServerClient presenceInterServerClient;

    @Value("${current.server-url}")
    private String currentServerUrl;

    @KafkaListener(
            topics = "presence-status",
            groupId = "presence-unicast-group"
    )
    public void consume(PresenceStatusEvent event, Acknowledgment ack) {
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

        ack.acknowledge();
    }

    private void sendToWebSocket(PresenceStatusDto status) {
        PresenceStatusMessageResponse response = new PresenceStatusMessageResponse(
                status.getUserId(),
                status.getStatus()
        );
        String destination = "/topic/presence/" + status.getChatRoomCode() + "/" + status.getUserId();
        String logContext = String.format("[Presence][%d][%s]", status.getUserId(), status.getStatus());

        WebSocketUtils.sendMessage(
                messagingTemplate,
                destination,
                response,
                logContext
        );
    }

    private void sendToRemoteServer(String targetServerUrl, PresenceStatusDto message) {
        presenceInterServerClient.sendPresence(targetServerUrl, message);
    }
}
