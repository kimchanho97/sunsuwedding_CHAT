package com.sunsuwedding.chat.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisChatRoomStore {

    private final RedisTemplate<String, String> redisTemplate;

    public void addChatRoomToUser(Long userId, String chatRoomCode) {
        long now = System.currentTimeMillis();
        redisTemplate.opsForZSet().add(RedisKeyUtil.userChatRoomsKey(userId), chatRoomCode, now);
    }

    public boolean isUserInChatRoom(String chatRoomCode, Long userId) {
        Set<String> userRoomCodes = redisTemplate.opsForZSet()
                .range(RedisKeyUtil.userChatRoomsKey(userId), 0, -1);

        return userRoomCodes != null && userRoomCodes.contains(chatRoomCode);
    }

    public Long nextMessageSeq(String chatRoomCode) {
        String key = RedisKeyUtil.chatRoomMessageSeqKey(chatRoomCode);
        return redisTemplate.opsForValue().increment(key);
    }

    public void updateChatRoomMeta(String chatRoomCode, String lastMessage, LocalDateTime lastMessageAt, Long lastSeqId) {
        String key = RedisKeyUtil.chatRoomMetaKey(chatRoomCode);
        Map<String, String> value = Map.of(
                "lastMessage", lastMessage,
                "lastMessageAt", lastMessageAt.toString(),
                "lastMessageSeqId", String.valueOf(lastSeqId)
        );
        redisTemplate.opsForHash().putAll(key, value);
    }

}
