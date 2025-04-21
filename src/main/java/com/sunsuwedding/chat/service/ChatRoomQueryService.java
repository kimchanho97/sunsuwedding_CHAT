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
        List<String> redisCodes = redisChatRoomStore.getSortedChatRoomCodes(userId, size);
        if (!redisCodes.isEmpty()) return redisCodes;

        // Redis 캐시에 없을 경우 RDB fallback + 캐시 갱신
        List<String> fallbackCodes = chatRoomInternalClient.getSortedChatRoomCodes(userId, size);
        fallbackCodes.forEach(code -> redisChatRoomStore.addChatRoomToUser(userId, code));
        return fallbackCodes;
    }

    public long countChatRooms(Long userId) {
        Long redisCount = redisChatRoomStore.countChatRooms(userId);
        if (redisCount != null && redisCount > 0) {
            return redisCount;
        }
        return chatRoomInternalClient.countChatRooms(userId);
    }

    public Map<String, ChatRoomMeta> getChatRoomMetas(List<String> chatRoomCodes) {
        boolean allExist = chatRoomCodes.stream()
                .allMatch(redisChatRoomStore::existsChatRoomMeta);

        if (allExist) {
            return redisChatRoomStore.getChatRoomMetas(chatRoomCodes);
        }

        // Redis에 일부라도 없으면 → 백엔드로 조회하고 Redis 캐시 갱신
        Map<String, ChatRoomMeta> metaMap = chatRoomInternalClient.getChatRoomMetas(chatRoomCodes);
        metaMap.forEach((code, meta) -> redisChatRoomStore.updateChatRoomMeta(
                code,
                meta.lastMessage(),
                meta.lastMessageAt(),
                meta.lastMessageSeqId()
        ));
        return metaMap;
    }
}
