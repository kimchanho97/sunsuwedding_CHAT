package com.sunsuwedding.chat.client;

import com.sunsuwedding.chat.dto.presece.PresenceStatusMessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class PresencePushClient {

    private final RestTemplate restTemplate;

    public void sendPresence(String serverUrl, PresenceStatusMessageResponse statusMessage) {
        String targetUrl = serverUrl + "/internal/presence/push";

        try {
            restTemplate.postForEntity(targetUrl, statusMessage, Void.class);
        } catch (Exception e) {
            log.error("❌ Presence 푸시 실패: {} → {}", statusMessage.getUserId(), serverUrl, e.getMessage());
            // 필요시 재시도 or DLQ 등록 가능
        }
    }
}
