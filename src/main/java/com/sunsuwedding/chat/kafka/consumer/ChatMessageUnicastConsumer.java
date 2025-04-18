package com.sunsuwedding.chat.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunsuwedding.chat.client.interserver.ChatMessageInterServerClient;
import com.sunsuwedding.chat.dto.message.ChatMessageResponse;
import com.sunsuwedding.chat.event.ChatMessageUnicastEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatMessageUnicastConsumer {

    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageInterServerClient chatMessageInterServerClient;

    @Value("${current.server-url}")
    private String currentServerUrl;

    @KafkaListener(topics = "chat-message-unicast", groupId = "chat-message-unicast-group")
    public void consume(String payload) {
        try {
            ChatMessageUnicastEvent event = objectMapper.readValue(payload, ChatMessageUnicastEvent.class);
            handleUnicast(event);
        } catch (JsonProcessingException e) {
            log.error("❌ ChatMessageUnicastEvent 역직렬화 실패: {}", payload, e);
        } catch (Exception e) {
            log.error("❌ ChatMessageUnicastEvent 처리 실패", e);
        }
    }

    private void handleUnicast(ChatMessageUnicastEvent event) {
        ChatMessageResponse message = event.response();
        String chatRoomCode = event.chatRoomCode();

        if (currentServerUrl.equals(event.targetServerUrl())) {
            sendToWebSocket(chatRoomCode, message);
        } else {
            sendToRemoteServer(event.targetServerUrl(), chatRoomCode, message);
        }
    }

    private void sendToWebSocket(String chatRoomCode, ChatMessageResponse message) {
        String destination = "/topic/chat-rooms/" + chatRoomCode;
        messagingTemplate.convertAndSend(destination, message);
    }

    private void sendToRemoteServer(String targetServerUrl, String chatRoomCode, ChatMessageResponse message) {
        chatMessageInterServerClient.sendUnicastMessage(targetServerUrl, chatRoomCode, message);
    }
}
