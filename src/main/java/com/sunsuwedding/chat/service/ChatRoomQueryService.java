package com.sunsuwedding.chat.service;

import com.sunsuwedding.chat.client.internal.ChatRoomInternalClient;
import com.sunsuwedding.chat.model.ChatRoomMeta;
import com.sunsuwedding.chat.redis.RedisChatRoomStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatRoomQueryService {

    private final RedisChatRoomStore redisChatRoomStore;
    private final ChatRoomInternalClient chatRoomInternalClient;

    public List<String> getSortedChatRoomCodes(Long userId, int size) {
        // Redis에 채팅방 코드가 없을 경우, RDB에서 조회
        List<String> redisCodes = redisChatRoomStore.getSortedChatRoomCodes(userId, size);
        if (!redisCodes.isEmpty()) return redisCodes;

        return chatRoomInternalClient.getSortedChatRoomCodes(userId, size);
    }

    public long countChatRooms(Long userId) {
        Long redisCount = redisChatRoomStore.countChatRooms(userId);
        if (redisCount != null && redisCount > 0) {
            return redisCount;
        }
        return chatRoomInternalClient.countChatRooms(userId);
    }

    public Map<String, ChatRoomMeta> getChatRoomMetas(List<String> chatRoomCodes) {
        // 1. 존재 여부 먼저 검사
        boolean allExist = chatRoomCodes.stream()
                .allMatch(redisChatRoomStore::existsChatRoomMeta);

        // 2. 모두 존재하면 → Redis에서 조회
        if (allExist) {
            return redisChatRoomStore.getChatRoomMetas(chatRoomCodes);
        }

        // 3. 하나라도 누락 → Fallback to Backend
        return chatRoomInternalClient.getChatRoomMetas(chatRoomCodes);
    }
}
