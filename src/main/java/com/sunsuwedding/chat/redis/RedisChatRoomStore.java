package com.sunsuwedding.chat.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisChatRoomStore {

    private final RedisTemplate<String, String> redisTemplate;

    public void initializeChatRoomEntry(String chatRoomCode, Long userId, Long plannerId) {
        saveSortedChatRoomReference(userId, chatRoomCode);
        saveSortedChatRoomReference(plannerId, chatRoomCode);

        initializeLastReadSeq(chatRoomCode, userId);
        initializeLastReadSeq(chatRoomCode, plannerId);
    }

    public boolean isUserInChatRoom(String chatRoomCode, Long userId) {
        Set<String> userRoomCodes = redisTemplate.opsForZSet()
                .range(RedisKeyUtil.userChatRoomsKey(userId), 0, -1);

        return userRoomCodes != null && userRoomCodes.contains(chatRoomCode);
    }

    private void saveSortedChatRoomReference(Long userId, String chatRoomCode) {
        long now = System.currentTimeMillis();
        redisTemplate.opsForZSet().add(RedisKeyUtil.userChatRoomsKey(userId), chatRoomCode, now);
    }

    private void initializeLastReadSeq(String chatRoomCode, Long userId) {
        redisTemplate.opsForValue().set(RedisKeyUtil.lastReadSeqKey(chatRoomCode, userId), "0");
    }


//    public void updateChatRoomMeta(String chatRoomCode, String message, Instant sentAt) {
//        String timestamp = sentAt.toString();
//
//        // 1. 메타 정보 갱신
//        Map<String, String> meta = Map.of(
//                "lastMessage", message,
//                "lastMessageAt", timestamp
//        );
//        redisTemplate.opsForHash().putAll(ChatRedisKeyUtil.roomMetaKey(chatRoomCode), meta);
//
//        // 2. ZSET 점수 갱신
//        double score = sentAt.toEpochMilli();
//        // (유저 ID들을 저장하거나 Kafka 컨슈머가 알 수 있으면 동시에 업데이트)
//        // 예시:
//        redisTemplate.opsForZSet().add(ChatRedisKeyUtil.sortedRoomKey(유저1), chatRoomCode, score);
//        redisTemplate.opsForZSet().add(ChatRedisKeyUtil.sortedRoomKey(유저2), chatRoomCode, score);
//    }

}
