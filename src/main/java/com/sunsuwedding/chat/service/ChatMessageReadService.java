package com.sunsuwedding.chat.service;

import com.sunsuwedding.chat.client.internal.ChatMessageReadClient;
import com.sunsuwedding.chat.redis.RedisChatReadStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatMessageReadService {

    private final RedisChatReadStore redisChatReadStore;
    private final ChatRoomParticipantService chatRoomParticipantService;
    private final ChatMessageReadClient chatMessageReadClient;

    public Map<Long, Long> getUserReadSequences(String chatRoomCode) {
        // 1. Redis에서 먼저 조회
        List<Long> participantIds = chatRoomParticipantService.getParticipantUserIds(chatRoomCode);
        Map<Long, Long> readMap = redisChatReadStore.getUserReadSequences(chatRoomCode, participantIds);
        if (!readMap.isEmpty()) {
            return readMap;
        }

        // 2. Redis에 없으면 → 백엔드 internal API 호출
        return chatMessageReadClient.getReadSequences(chatRoomCode);
    }
}
