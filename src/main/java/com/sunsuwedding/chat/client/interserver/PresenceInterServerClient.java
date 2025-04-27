package com.sunsuwedding.chat.client.interserver;

import com.sunsuwedding.chat.common.util.RetryUtils;
import com.sunsuwedding.chat.dto.presence.PresenceStatusDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class PresenceInterServerClient {

    private final RestTemplate realtimeRestTemplate;

    public PresenceInterServerClient(@Qualifier("realtimeRestTemplate") RestTemplate realtimeRestTemplate) {
        this.realtimeRestTemplate = realtimeRestTemplate;
    }

    public void sendPresence(String serverUrl, PresenceStatusDto status) {
        String targetUrl = serverUrl + "/internal/presence/push";
        String logContext = String.format("[Presence][%d][%s]", status.getUserId(), status.getStatus());

        RetryUtils.executeWithRetry(
                () -> realtimeRestTemplate.postForEntity(targetUrl, status, Void.class),
                logContext
        );
    }
}
