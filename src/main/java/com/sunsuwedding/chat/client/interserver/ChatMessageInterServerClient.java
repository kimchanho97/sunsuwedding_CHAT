package com.sunsuwedding.chat.client.interserver;

import com.sunsuwedding.chat.common.util.RetryUtils;
import com.sunsuwedding.chat.dto.message.ChatMessageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class ChatMessageInterServerClient {

    private final RestTemplate realtimeRestTemplate;

    public ChatMessageInterServerClient(@Qualifier("realtimeRestTemplate") RestTemplate realtimeRestTemplate) {
        this.realtimeRestTemplate = realtimeRestTemplate;
    }

    public void sendUnicastMessage(String serverBaseUrl, String chatRoomCode, ChatMessageResponse message) {
        String endpoint = serverBaseUrl + "/internal/chat/messages/unicast/" + chatRoomCode;
        String logContext = String.format("[Message][%s][%s]", chatRoomCode, message.getSequenceId());

        RetryUtils.executeWithRetry(
                () -> realtimeRestTemplate.postForEntity(endpoint, message, Void.class),
                logContext
        );
    }
}
