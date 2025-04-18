package com.sunsuwedding.chat.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RedisChatReadStore {

    private final RedisTemplate<String, String> redisTemplate;

    public Map<Long, Long> getUserReadSequences(String chatRoomCode, List<Long> userIds) {
        Map<Long, Long> result = new HashMap<>();
        List<String> keys = userIds.stream()
                .map(userId -> RedisKeyUtil.lastReadSeqKey(chatRoomCode, userId))
                .toList();

        List<String> values = redisTemplate.opsForValue().multiGet(keys);
        if (values == null) return Map.of();

        for (int i = 0; i < userIds.size(); i++) {
            String value = values.get(i);
            if (value != null) {
                result.put(userIds.get(i), Long.valueOf(value));
            }
        }
        return result;
    }

    public void initializeLastReadSequence(String chatRoomCode, Long userId) {
        redisTemplate.opsForValue().set(RedisKeyUtil.lastReadSeqKey(chatRoomCode, userId), "0");
    }

    public Optional<Long> getLastReadSequence(String chatRoomCode, Long userId) {
        String key = RedisKeyUtil.lastReadSeqKey(chatRoomCode, userId);
        String value = redisTemplate.opsForValue().get(key);
        if (value == null) return Optional.empty();
        return Optional.of(Long.valueOf(value));
    }

    public void updateLastReadSequence(String chatRoomCode, Long userId, Long sequenceId) {
        String key = RedisKeyUtil.lastReadSeqKey(chatRoomCode, userId);
        redisTemplate.opsForValue().set(key, String.valueOf(sequenceId));
    }
}
