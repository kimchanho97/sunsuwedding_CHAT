package com.sunsuwedding.chat.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class RedisChatRoomService {

    private final RedisTemplate<String, String> redisTemplate;

    public void saveChatRoomMeta(String chatRoomId, Long userId, Long plannerId) {
        long now = System.currentTimeMillis();

        // 유저별 정렬 채팅방 목록
        redisTemplate.opsForZSet().add(ChatRedisKeyUtil.sortedRoomKey(userId), chatRoomId, now);
        redisTemplate.opsForZSet().add(ChatRedisKeyUtil.sortedRoomKey(plannerId), chatRoomId, now);

        // 유저별 상태 초기화
        Map<String, String> status = Map.of(
                "unreadCount", "0",
                "lastReadMessageId", "0"
        );

        redisTemplate.opsForHash().putAll(ChatRedisKeyUtil.userRoomStatusKey(chatRoomId, userId), status);
        redisTemplate.opsForHash().putAll(ChatRedisKeyUtil.userRoomStatusKey(chatRoomId, plannerId), status);
    }

    public void markUserOnline(Long userId) {
        redisTemplate.opsForValue().set(
                ChatRedisKeyUtil.userOnlineKey(userId),
                "1",
                java.time.Duration.ofSeconds(10)
        );
    }

//    public void updateChatRoomMeta(String chatRoomId, String message, Instant sentAt) {
//        String timestamp = sentAt.toString();
//
//        // 1. 메타 정보 갱신
//        Map<String, String> meta = Map.of(
//                "lastMessage", message,
//                "lastMessageAt", timestamp
//        );
//        redisTemplate.opsForHash().putAll(ChatRedisKeyUtil.roomMetaKey(chatRoomId), meta);
//
//        // 2. ZSET 점수 갱신
//        double score = sentAt.toEpochMilli();
//        // (유저 ID들을 저장하거나 Kafka 컨슈머가 알 수 있으면 동시에 업데이트)
//        // 예시:
//        redisTemplate.opsForZSet().add(ChatRedisKeyUtil.sortedRoomKey(유저1), chatRoomId, score);
//        redisTemplate.opsForZSet().add(ChatRedisKeyUtil.sortedRoomKey(유저2), chatRoomId, score);
//    }

}
