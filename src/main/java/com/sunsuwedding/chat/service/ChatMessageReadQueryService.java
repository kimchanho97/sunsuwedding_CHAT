package com.sunsuwedding.chat.service;

import com.sunsuwedding.chat.client.internal.ChatMessageReadClient;
import com.sunsuwedding.chat.client.internal.ChatRoomInternalClient;
import com.sunsuwedding.chat.redis.RedisChatReadStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatMessageReadQueryService {

    private final RedisChatReadStore redisChatReadStore;
    private final ChatRoomParticipantQueryService chatRoomParticipantQueryService;
    private final ChatMessageReadClient chatMessageReadClient;
    private final ChatRoomInternalClient chatRoomInternalClient;

    /**
     * 채팅방 내 참여 유저들의 읽은 시퀀스를 조회
     */
    public Map<Long, Long> getReadSequencesByUserInChatRoom(String chatRoomCode) {
        // 1. Redis에서 먼저 조회
        List<Long> participantIds = chatRoomParticipantQueryService.getParticipantUserIds(chatRoomCode);
        Map<Long, Long> readMap = redisChatReadStore.getReadSequencesByUserInChatRoom(chatRoomCode, participantIds);

        // 2. 존재하면 바로 반환
        if (!readMap.isEmpty()) {
            return readMap;
        }

        // 3. Redis에 없으면 → 백엔드 internal API 호출
        Map<Long, Long> fallback = chatMessageReadClient.getReadSequencesByUserInChatRoom(chatRoomCode);

        // 4. Redis 캐시 갱신
        fallback.forEach((userId, seq) ->
                redisChatReadStore.markMessageAsRead(chatRoomCode, userId, seq)
        );
        return fallback;
    }

    /**
     * 특정 유저의 여러 채팅방에 대한 읽은 시퀀스를 조회
     */
    public Map<String, Long> getReadSequencesByChatRoomsForUser(List<String> chatRoomCodes, Long userId) {
        boolean allExist = chatRoomCodes.stream()
                .allMatch(code -> redisChatReadStore.existsReadSequence(code, userId));

        // 1. 모두 Redis에 존재하면 바로 반환
        if (allExist) {
            return redisChatReadStore.getReadSequencesByChatRoomsForUser(chatRoomCodes, userId);
        }

        // 2. Redis에 일부라도 없으면 → 백엔드 internal API 호출
        Map<String, Long> fallback = chatRoomInternalClient.getReadSequencesByChatRoomsForUser(chatRoomCodes, userId);

        // 3. Redis 캐시 갱신
        fallback.forEach((code, seq) ->
                redisChatReadStore.markMessageAsRead(code, userId, seq)
        );
        return fallback;
    }
}
