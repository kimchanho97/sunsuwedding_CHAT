package com.sunsuwedding.chat.kafka.consumer;

import com.sunsuwedding.chat.client.interserver.ChatMessageInterServerClient;
import com.sunsuwedding.chat.common.util.WebSocketUtils;
import com.sunsuwedding.chat.dto.message.ChatMessageResponse;
import com.sunsuwedding.chat.event.ChatMessageUnicastEvent;
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
public class ChatMessageUnicastConsumer {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageInterServerClient chatMessageInterServerClient;

    @Value("${current.server-url}")
    private String currentServerUrl;

    @KafkaListener(
            topics = "chat-message-unicast",
            groupId = "chat-message-unicast-group"
    )
    public void consume(ChatMessageUnicastEvent event, Acknowledgment ack) {
        ChatMessageResponse message = event.response();
        String chatRoomCode = event.chatRoomCode();

        if (currentServerUrl.equals(event.targetServerUrl())) {
            sendToWebSocket(chatRoomCode, message);
        } else {
            sendToRemoteServer(event.targetServerUrl(), chatRoomCode, message);
        }

        ack.acknowledge();
    }

    private void sendToWebSocket(String chatRoomCode, ChatMessageResponse message) {
        String destination = "/topic/chat/rooms/" + chatRoomCode;
        String logContext = String.format("[Message][%s][%s]", chatRoomCode, message.getSequenceId());

        WebSocketUtils.sendMessage(
                messagingTemplate,
                destination,
                message,
                logContext
        );
    }

    private void sendToRemoteServer(String targetServerUrl, String chatRoomCode, ChatMessageResponse message) {
        chatMessageInterServerClient.sendUnicastMessage(targetServerUrl, chatRoomCode, message);
    }
}
