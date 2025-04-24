package com.sunsuwedding.chat.service;

import com.sunsuwedding.chat.client.internal.ChatRoomInternalClient;
import com.sunsuwedding.chat.client.internal.ChatRoomMetaClient;
import com.sunsuwedding.chat.model.ChatRoomMeta;
import com.sunsuwedding.chat.redis.RedisChatRoomStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomQueryService {

    private final RedisChatRoomStore redisChatRoomStore;
    private final ChatRoomInternalClient chatRoomInternalClient;
    private final ChatRoomMetaClient chatRoomMetaClient;

    public List<String> getSortedChatRoomCodes(Long userId, int size, long totalCount) {
        List<String> redisCodes = redisChatRoomStore.getSortedChatRoomCodes(userId, size);
        if (redisCodes.size() >= size || redisCodes.size() == totalCount) return redisCodes;

        // Redis 캐시에 없을 경우 RDB fallback + 캐시 갱신
        List<String> fallbackCodes = chatRoomInternalClient.getSortedChatRoomCodes(userId, size);

        // 현재 시간보다 더 과거의 baseTime (정렬용 score이기 때문에 시간 오차 허용)
        long baseTime = System.currentTimeMillis() - 1_000_000; // 약 17분 전
        AtomicInteger offset = new AtomicInteger(0);

        fallbackCodes.forEach(code -> {
            // 최신 채팅방일수록 score가 높아지도록
            long score = baseTime + (size - offset.getAndIncrement());
            redisChatRoomStore.addChatRoomWithScore(userId, code, score);
        });
        return fallbackCodes;
    }

    public Map<String, ChatRoomMeta> getChatRoomMetas(List<String> chatRoomCodes) {
        boolean allExist = chatRoomCodes.stream()
                .allMatch(redisChatRoomStore::existsChatRoomMeta);

        if (allExist) {
            return redisChatRoomStore.getChatRoomMetas(chatRoomCodes);
        }

        // Redis에 일부라도 없으면 → 백엔드로 조회하고 Redis 캐시 갱신
        Map<String, ChatRoomMeta> metaMap = chatRoomMetaClient.getChatRoomMetas(chatRoomCodes);
        metaMap.forEach((code, meta) -> redisChatRoomStore.updateChatRoomMeta(
                code,
                meta.lastMessage(),
                meta.lastMessageAt(),
                meta.lastMessageSeqId()
        ));
        return metaMap;
    }
}
