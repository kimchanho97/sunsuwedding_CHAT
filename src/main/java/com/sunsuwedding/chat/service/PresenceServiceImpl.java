package com.sunsuwedding.chat.service;

import com.sunsuwedding.chat.client.ChatRoomApiClient;
import com.sunsuwedding.chat.event.message.PresenceStatusEvent;
import com.sunsuwedding.chat.kafka.producer.PresenceStatusProducer;
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
    private final PresenceStatusProducer presenceStatusProducer;
    private final ChatRoomApiClient chatRoomApiClient;

    @Value("${current.server-url}")
    private String currentServerUrl;

    @Override
    public void handleConnect(Long userId, String chatRoomCode, String sessionId) {
        // 1. Redis에 본인 상태 저장
        redisPresenceStore.saveSession(sessionId, userId, chatRoomCode);
        redisPresenceStore.savePresence(userId, chatRoomCode, currentServerUrl);

        // 2. 채팅방 참여자 조회
        List<Long> participantUserIds = chatRoomApiClient.getParticipantUserIds(chatRoomCode);
        for (Long otherUserId : participantUserIds) {
            if (otherUserId.equals(userId)) continue;

            // 2-1. 상대방이 online이면, 상대방의 상태를 내 서버로 전파
            if (redisPresenceStore.isOnline(otherUserId, chatRoomCode)) {
                presenceStatusProducer.send(
                        new PresenceStatusEvent(otherUserId, chatRoomCode, "online", currentServerUrl)
                );
            }
            // 2-2. 내 상태를 상대방 서버로 전파
            String otherServerUrl = redisPresenceStore.findPresenceServerUrl(otherUserId, chatRoomCode);
            if (otherServerUrl != null) {
                presenceStatusProducer.send(
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

        // 2. 참여자 목록 조회 후 순회
        List<Long> participantUserIds = chatRoomApiClient.getParticipantUserIds(chatRoomCode);
        for (Long otherUserId : participantUserIds) {
            if (otherUserId.equals(userId)) continue;

            String targetServerUrl = redisPresenceStore.findPresenceServerUrl(otherUserId, chatRoomCode);

            if (targetServerUrl != null) {
                presenceStatusProducer.send(
                        new PresenceStatusEvent(userId, chatRoomCode, "offline", targetServerUrl)
                );
            }
        }
    }
}
