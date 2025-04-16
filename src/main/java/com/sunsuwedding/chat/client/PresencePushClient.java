package com.sunsuwedding.chat.client;

import com.sunsuwedding.chat.dto.presece.PresenceStatusMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class PresencePushClient {

    private final RestTemplate restTemplate;

    public void sendPresence(String serverUrl, PresenceStatusMessage status) {
        String targetUrl = serverUrl + "/internal/presence/push";

        try {
            ResponseEntity<Void> response = restTemplate.postForEntity(targetUrl, status, Void.class);
            log.info("📤 Presence 유저 상태 푸시 완료: {} → {}, status={}", status.getUserId(), serverUrl, status.getStatus());
        } catch (Exception e) {
            log.error("❌ Presence 푸시 실패: {} → {}", status.getUserId(), serverUrl, e.getMessage());
            // 필요시 재시도 or DLQ 등록 가능
        }
    }
}
