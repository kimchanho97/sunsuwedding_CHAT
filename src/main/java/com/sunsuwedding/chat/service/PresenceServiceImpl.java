package com.sunsuwedding.chat.service;

import com.sunsuwedding.chat.event.message.PresenceStatusEvent;
import com.sunsuwedding.chat.kafka.producer.PresenceStatusProducer;
import com.sunsuwedding.chat.redis.RedisPresenceStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PresenceServiceImpl implements PresenceService {

    private final RedisPresenceStore redisPresenceStore;
    private final PresenceStatusProducer presenceStatusProducer;

    @Value("${chat.server-id}")
    private String currentServerId;

    @Override
    public void handleConnect(Long userId, Long chatPartnerId, String sessionId) {
        // 1. Redis에 본인 상태 저장
        redisPresenceStore.saveSession(sessionId, userId);
        redisPresenceStore.savePresence(userId, currentServerId);
        redisPresenceStore.savePartnerBySession(sessionId, chatPartnerId);

        // 2. 상대방이 online 상태이면 → 내 서버에 알림
        if (redisPresenceStore.isOnline(chatPartnerId)) {
            presenceStatusProducer.send(
                    new PresenceStatusEvent(chatPartnerId, "online", currentServerId));
        }

        // 3. 내 상태를 상대방의 서버로 전파
        String partnerServerId = redisPresenceStore.getPresenceServerId(chatPartnerId);
        if (partnerServerId != null) {
            presenceStatusProducer.send(
                    new PresenceStatusEvent(userId, "online", partnerServerId));
        }
    }

    @Override
    public void handlePing(Long userId, String sessionId) {
        redisPresenceStore.refreshTtl(userId, sessionId);
    }

    @Override
    public void handleDisconnect(String sessionId) {
        Long userId = redisPresenceStore.findUserIdBySession(sessionId);
        if (userId == null) {
            return;
        }
        // 1. Redis 정보 제거
        redisPresenceStore.removeSession(sessionId);
        redisPresenceStore.removePresence(userId);

        // 2. 상대방에게 offline 전파
        Long chatPartnerId = redisPresenceStore.getPartnerIdBySession(sessionId);
        redisPresenceStore.removePartnerBySession(sessionId);
        if (chatPartnerId != null) {
            String targetServerId = redisPresenceStore.getPresenceServerId(chatPartnerId);
            if (targetServerId != null) {
                presenceStatusProducer.send(new PresenceStatusEvent(userId, "offline", targetServerId));
            }
        }
    }
}
