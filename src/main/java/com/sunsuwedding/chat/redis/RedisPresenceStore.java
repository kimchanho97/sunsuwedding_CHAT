package com.sunsuwedding.chat.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisPresenceStore {

    private final RedisTemplate<String, String> redisTemplate;
    private static final Duration TTL = Duration.ofSeconds(180);

    public void saveSession(String sessionId, Long userId) {
        redisTemplate.opsForValue().set(RedisKeyUtil.sessionToUserKey(sessionId), String.valueOf(userId), TTL);
    }

    public void savePresence(Long userId, String serverId) {
        redisTemplate.opsForValue().set(RedisKeyUtil.userPresenceKey(userId), serverId, TTL);
    }

    public void refreshTtl(Long userId, String sessionId) {
        redisTemplate.expire(RedisKeyUtil.userPresenceKey(userId), TTL);
        redisTemplate.expire(RedisKeyUtil.sessionToUserKey(sessionId), TTL);
    }

    public boolean isOnline(Long userId) {
        return redisTemplate.hasKey(RedisKeyUtil.userPresenceKey(userId));
    }

    public String getPresenceServerId(Long userId) {
        return redisTemplate.opsForValue().get(RedisKeyUtil.userPresenceKey(userId));
    }
}
