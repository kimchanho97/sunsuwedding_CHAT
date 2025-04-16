package com.sunsuwedding.chat.client;

import com.sunsuwedding.chat.dto.presece.PresenceMessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class PresencePushClient {

    private final RestTemplate restTemplate;

    public void sendPresence(String serverUrl, PresenceMessageResponse status) {
        String targetUrl = serverUrl + "/internal/presence/push";

        try {
            restTemplate.postForEntity(targetUrl, status, Void.class);
        } catch (Exception e) {
            log.error("❌ Presence 푸시 실패: {} → {}", status.getUserId(), serverUrl, e.getMessage());
            // 필요시 재시도 or DLQ 등록 가능
        }
    }
}
