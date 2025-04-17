package com.sunsuwedding.chat.client;

import com.sunsuwedding.chat.dto.presence.PresenceStatusDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class PresencePushClient {

    private final RestTemplate restTemplate;

    public void sendPresence(String serverUrl, PresenceStatusDto status) {
        String targetUrl = serverUrl + "/internal/presence/push";
        try {
            restTemplate.postForEntity(targetUrl, status, Void.class);
        } catch (Exception e) {
            log.error("❌ Presence 푸시 실패: userId={}, server={}, error={}",
                    status.getUserId(), serverUrl, e.getMessage());
            // TODO: 재시도, DLQ 등록 가능
        }
    }
}
