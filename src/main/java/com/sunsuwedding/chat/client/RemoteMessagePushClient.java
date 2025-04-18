package com.sunsuwedding.chat.client;

import com.sunsuwedding.chat.dto.message.ChatMessageUnicastDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class RemoteMessagePushClient {

    private final RestTemplate restTemplate;

    public void sendUnicastMessage(String serverBaseUrl, Long userId, ChatMessageUnicastDto message) {
        String endpoint = serverBaseUrl + "/internal/chat/messages/unicast/" + userId;
        try {
            restTemplate.postForEntity(endpoint, message, Void.class);
        } catch (Exception e) {
            log.error("❌ 유니캐스트 메시지 전송 실패 - url={}, userId={}, error={}", endpoint, userId, e.getMessage());
            // TODO: 나중에 retry, DLQ, metrics 전송 등 확장 가능
        }
    }
}
