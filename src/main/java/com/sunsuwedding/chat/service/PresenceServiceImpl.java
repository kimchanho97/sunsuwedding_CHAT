package com.sunsuwedding.chat.service;

import com.sunsuwedding.chat.event.PresenceStatusEvent;
import com.sunsuwedding.chat.kafka.producer.PresenceUnicastProducer;
import com.sunsuwedding.chat.redis.RedisChatReadStore;
import com.sunsuwedding.chat.redis.RedisPresenceStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PresenceServiceImpl implements PresenceService {

    private final RedisPresenceStore redisPresenceStore;
    private final PresenceUnicastProducer presenceUnicastProducer;
    private final ChatRoomParticipantService chatRoomParticipantService;
    private final RedisChatReadStore redisChatReadStore;

    @Value("${current.server-url}")
    private String currentServerUrl;

    @Override
    public void handleConnect(Long userId, String chatRoomCode, String sessionId) {
        // 1. Redis에 본인 상태 저장
        redisPresenceStore.saveSession(sessionId, userId, chatRoomCode);
        redisPresenceStore.savePresence(userId, chatRoomCode, currentServerUrl);

        // 2. all 메시지 읽음 처리
        redisChatReadStore.markAllMessagesAsRead(chatRoomCode, userId);

        // 3. 채팅방 참여자 조회
        List<Long> participantUserIds = chatRoomParticipantService.getParticipantUserIds(chatRoomCode);
        for (Long otherUserId : participantUserIds) {
            if (otherUserId.equals(userId)) continue;

            // 3-1. 상대방이 online이면, 상대방의 상태를 내 서버로 전파
            if (redisPresenceStore.isOnline(otherUserId, chatRoomCode)) {
                presenceUnicastProducer.send(
                        new PresenceStatusEvent(otherUserId, chatRoomCode, "online", currentServerUrl)
                );
            }
            // 3-2. 내 상태를 상대방 서버로 전파
            String otherServerUrl = redisPresenceStore.findPresenceServerUrl(otherUserId, chatRoomCode);
            if (otherServerUrl != null) {
                presenceUnicastProducer.send(
                        new PresenceStatusEvent(userId, chatRoomCode, "online", otherServerUrl)
                );
            }
        }
    }

    @Override
    public void handlePing(Long userId, String chatRoomCode, String sessionId) {
        redisPresenceStore.refreshTtl(userId, chatRoomCode, sessionId);
    }

    @Override
    public void handleDisconnect(String sessionId) {
        Long userId = redisPresenceStore.findUserIdBySession(sessionId);
        String chatRoomCode = redisPresenceStore.findChatRoomCodeBySession(sessionId);
        if (userId == null || chatRoomCode == null) {
            return;
        }

        // 1. Redis 정보 제거
        redisPresenceStore.removeSession(sessionId);
        redisPresenceStore.removePresence(userId, chatRoomCode);

        // 2. 온라인 유저들에게 offline 상태 전파
        List<Long> participantUserIds = chatRoomParticipantService.getParticipantUserIds(chatRoomCode);
        for (Long otherUserId : participantUserIds) {
            if (otherUserId.equals(userId)) continue;

            String targetServerUrl = redisPresenceStore.findPresenceServerUrl(otherUserId, chatRoomCode);

            if (targetServerUrl != null) {
                presenceUnicastProducer.send(
                        new PresenceStatusEvent(userId, chatRoomCode, "offline", targetServerUrl)
                );
            }
        }
    }
}
