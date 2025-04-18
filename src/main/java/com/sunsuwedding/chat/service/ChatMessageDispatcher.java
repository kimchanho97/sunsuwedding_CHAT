package com.sunsuwedding.chat.service;

import com.sunsuwedding.chat.client.RemoteMessagePushClient;
import com.sunsuwedding.chat.dto.message.ChatMessageUnicastDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatMessageDispatcher {

    private final SimpMessagingTemplate messagingTemplate;
    private final RemoteMessagePushClient remoteMessagePushClient;

    @Value("${chat.server-id}")
    private String currentServerId;

    public void dispatch(Map<Long, String> userServerMap, ChatMessageUnicastDto message) {
        userServerMap.forEach((userId, serverId) -> {
            if (currentServerId.equals(serverId)) {
                sendToLocalWebSocket(userId, message);
            } else {
                sendToRemoteServer(serverId, userId, message);
            }
        });
    }

    private void sendToLocalWebSocket(Long userId, ChatMessageUnicastDto message) {
        String destination = "/topic/chat/rooms/" + message.getChatRoomCode() + "/" + userId;
        messagingTemplate.convertAndSend(destination, message);
    }

    private void sendToRemoteServer(String serverId, Long userId, ChatMessageUnicastDto message) {
        remoteMessagePushClient.sendUnicastMessage(serverId, userId, message);
    }

}
