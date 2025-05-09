package com.sunsuwedding.chat.redis;

import com.sunsuwedding.chat.dto.sync.ChatReadSeqSyncRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisChatReadStore {

    private final RedisTemplate<String, String> redisTemplate;

    public void markAllMessagesAsRead(String chatRoomCode, Long userId) {
        String metaKey = RedisKeyUtil.chatRoomMetaKey(chatRoomCode);
        String lastSeqStr = (String) redisTemplate.opsForHash().get(metaKey, "lastMessageSeqId");

        long lastSeq = lastSeqStr != null ? Long.parseLong(lastSeqStr) : 0L;

        String readKey = RedisKeyUtil.lastReadSeqKey(chatRoomCode, userId);
        redisTemplate.opsForValue().set(readKey, String.valueOf(lastSeq));

        // dirty 저장소에 추가
        markLastReadSequenceAsDirty(chatRoomCode, userId);
    }

    public Map<Long, Long> getReadSequencesByUserInChatRoom(String chatRoomCode, List<Long> userIds) {
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

    public void markMessageAsRead(String chatRoomCode, Long userId, Long sequenceId) {
        String key = RedisKeyUtil.lastReadSeqKey(chatRoomCode, userId);
        redisTemplate.opsForValue().set(key, String.valueOf(sequenceId));
    }

    public boolean existsReadSequence(String chatRoomCode, Long userId) {
        String key = RedisKeyUtil.lastReadSeqKey(chatRoomCode, userId);
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public Map<String, Long> getReadSequencesByChatRoomsForUser(List<String> chatRoomCodes, Long userId) {
        Map<String, Long> result = new HashMap<>();
        List<String> keys = chatRoomCodes.stream()
                .map(code -> RedisKeyUtil.lastReadSeqKey(code, userId))
                .toList();

        List<String> values = redisTemplate.opsForValue().multiGet(keys);

        for (int i = 0; i < chatRoomCodes.size(); i++) {
            String value = values.get(i);
            if (value != null) {
                result.put(chatRoomCodes.get(i), Long.parseLong(value));
            }
        }
        return result;
    }

    public void markLastReadSequenceAsDirty(String chatRoomCode, Long userId) {
        String dirtyKey = RedisKeyUtil.dirtyLastReadSeqKey(); // dirty:chat:room:last-read
        String member = chatRoomCode + ":" + userId;
        redisTemplate.opsForSet().add(dirtyKey, member);
    }

    public Set<String> getDirtyLastReadKeys() {
        return redisTemplate.opsForSet().members(RedisKeyUtil.dirtyLastReadSeqKey());
    }

    public Long getLastReadSequence(String chatRoomCode, Long userId) {
        String value = redisTemplate.opsForValue().get(RedisKeyUtil.lastReadSeqKey(chatRoomCode, userId));
        return value != null ? Long.parseLong(value) : 0L;
    }

    public void removeDirtyLastReadKeys(List<ChatReadSeqSyncRequest> requests) {
        String[] keysToRemove = requests.stream()
                .map(req -> req.chatRoomCode() + ":" + req.userId())
                .toArray(String[]::new);
        redisTemplate.opsForSet().remove(RedisKeyUtil.dirtyLastReadSeqKey(), (Object[]) keysToRemove);
    }

}
