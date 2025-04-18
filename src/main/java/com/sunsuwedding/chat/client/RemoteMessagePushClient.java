package com.sunsuwedding.chat.client;

import com.sunsuwedding.chat.dto.message.ChatMessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class RemoteMessagePushClient {

    private final RestTemplate restTemplate;

    public void sendUnicastMessage(String serverBaseUrl, String chatRoomCode, ChatMessageResponse message) {
        String endpoint = serverBaseUrl + "/internal/chat/messages/unicast/" + chatRoomCode;
        try {
            restTemplate.postForEntity(endpoint, message, Void.class);
        } catch (Exception e) {
            log.error("❌ 유니캐스트 메시지 전송 실패 - url={}, chatRoomCode={}, error={}", endpoint, chatRoomCode, e.getMessage());
        }
    }
}
