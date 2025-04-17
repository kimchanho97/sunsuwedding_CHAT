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

    public Long findUserIdBySession(String sessionId) {
        String userIdStr = redisTemplate.opsForValue().get(RedisKeyUtil.sessionToUserKey(sessionId));
        if (userIdStr == null) return null;
        return Long.valueOf(userIdStr);
    }

    public void removeSession(String sessionId) {
        redisTemplate.delete(RedisKeyUtil.sessionToUserKey(sessionId));
    }

    public void savePresence(Long userId, String serverId) {
        redisTemplate.opsForValue().set(RedisKeyUtil.userPresenceKey(userId), serverId, TTL);
    }

    public String getPresenceServerId(Long userId) {
        return redisTemplate.opsForValue().get(RedisKeyUtil.userPresenceKey(userId));
    }

    public void removePresence(Long userId) {
        redisTemplate.delete(RedisKeyUtil.userPresenceKey(userId));
    }

    public void savePartnerBySession(String sessionId, Long chatPartnerId) {
        redisTemplate.opsForValue().set(RedisKeyUtil.sessionToPartnerKey(sessionId), String.valueOf(chatPartnerId), TTL);
    }

    public Long getPartnerIdBySession(String sessionId) {
        String val = redisTemplate.opsForValue().get(RedisKeyUtil.sessionToPartnerKey(sessionId));
        if (val == null) return null;
        return Long.valueOf(val);
    }

    public void removePartnerBySession(String sessionId) {
        redisTemplate.delete(RedisKeyUtil.sessionToPartnerKey(sessionId));
    }

    public void refreshTtl(Long userId, String sessionId) {
        redisTemplate.expire(RedisKeyUtil.userPresenceKey(userId), TTL);
        redisTemplate.expire(RedisKeyUtil.sessionToUserKey(sessionId), TTL);
    }

    public boolean isOnline(Long userId) {
        return redisTemplate.hasKey(RedisKeyUtil.userPresenceKey(userId));
    }

}
