package com.sunsuwedding.chat.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RedisPresenceStore {

    private final RedisTemplate<String, String> redisTemplate;
    private static final Duration TTL = Duration.ofSeconds(180);

    public void saveSession(String sessionId, Long userId, String chatRoomCode, Long chatPartnerId) {
        String key = RedisKeyUtil.sessionKey(sessionId);
        redisTemplate.opsForHash().put(key, "userId", String.valueOf(userId));
        redisTemplate.opsForHash().put(key, "chatRoomCode", chatRoomCode);
        redisTemplate.opsForHash().put(key, "chatPartnerId", String.valueOf(chatPartnerId));
        redisTemplate.expire(key, TTL);
    }

    public Long findUserIdBySession(String sessionId) {
        String val = (String) redisTemplate.opsForHash().get(RedisKeyUtil.sessionKey(sessionId), "userId");
        return val != null ? Long.valueOf(val) : null;
    }

    public String findChatRoomCodeBySession(String sessionId) {
        return (String) redisTemplate.opsForHash().get(RedisKeyUtil.sessionKey(sessionId), "chatRoomCode");
    }

    public Long findChatPartnerIdBySession(String sessionId) {
        String val = (String) redisTemplate.opsForHash().get(RedisKeyUtil.sessionKey(sessionId), "chatPartnerId");
        return val != null ? Long.valueOf(val) : null;
    }

    public void removeSession(String sessionId) {
        redisTemplate.delete(RedisKeyUtil.sessionKey(sessionId));
    }

    public void savePresence(Long userId, String chatRoomCode, String serverId) {
        redisTemplate.opsForValue().set(RedisKeyUtil.userPresenceKey(chatRoomCode, userId), serverId, TTL);
    }

    public String findPresenceServerId(Long userId, String chatRoomCode) {
        return redisTemplate.opsForValue().get(RedisKeyUtil.userPresenceKey(chatRoomCode, userId));
    }

    public void removePresence(Long userId, String chatRoomCode) {
        redisTemplate.delete(RedisKeyUtil.userPresenceKey(chatRoomCode, userId));
    }

    public boolean isOnline(Long userId, String chatRoomCode) {
        String key = RedisKeyUtil.userPresenceKey(chatRoomCode, userId);
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void refreshTtl(Long userId, String chatRoomCode, String sessionId) {
        redisTemplate.expire(RedisKeyUtil.userPresenceKey(chatRoomCode, userId), TTL);
        redisTemplate.expire(RedisKeyUtil.sessionKey(sessionId), TTL);
    }

    public Map<Long, String> findOnlineUsersWithServerId(String chatRoomCode) {
        String pattern = "chat:presence:" + chatRoomCode + ":*";
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys.isEmpty()) return Map.of();

        return keys.stream()
                .collect(Collectors.toMap(
                        key -> Long.valueOf(key.substring(key.lastIndexOf(":") + 1)),
                        key -> redisTemplate.opsForValue().get(key)
                ));
    }
}
