package com.sunsuwedding.chat.redis;

import com.sunsuwedding.chat.model.ChatRoomMeta;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
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

    public void resortChatRoom(Long userId, String chatRoomCode, Long timestampMillis) {
        String zsetKey = RedisKeyUtil.userChatRoomsKey(userId);
        redisTemplate.opsForZSet().add(zsetKey, chatRoomCode, timestampMillis);
    }

    public void addMemberToChatRoom(String chatRoomCode, Long userId) {
        String key = RedisKeyUtil.chatRoomMembersKey(chatRoomCode);
        redisTemplate.opsForSet().add(key, String.valueOf(userId));
    }

    public Set<String> getChatRoomMembers(String chatRoomCode) {
        String key = RedisKeyUtil.chatRoomMembersKey(chatRoomCode);
        return redisTemplate.opsForSet().members(key);
    }

    public boolean isMemberOfChatRoom(String chatRoomCode, Long userId) {
        String key = RedisKeyUtil.chatRoomMembersKey(chatRoomCode);
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, String.valueOf(userId)));
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

    public void addMembersToChatRoom(String chatRoomCode, List<Long> userIds) {
        String key = RedisKeyUtil.chatRoomMembersKey(chatRoomCode);
        String[] userIdStrings = userIds.stream().map(String::valueOf).toArray(String[]::new);
        redisTemplate.opsForSet().add(key, userIdStrings);
    }

    public long countChatRooms(Long userId) {
        return redisTemplate.opsForZSet().size(RedisKeyUtil.userChatRoomsKey(userId));
    }

    public List<String> getSortedChatRoomCodes(Long userId, int size) {
        String key = RedisKeyUtil.userChatRoomsKey(userId);
        return redisTemplate.opsForZSet()
                .reverseRange(key, 0, size - 1)
                .stream().toList();
    }

    public Map<String, ChatRoomMeta> getChatRoomMetas(List<String> chatRoomCodes) {
        Map<String, ChatRoomMeta> result = new HashMap<>();

        for (String code : chatRoomCodes) {
            String key = RedisKeyUtil.chatRoomMetaKey(code);
            Map<Object, Object> rawMeta = redisTemplate.opsForHash().entries(key);

            if (!rawMeta.isEmpty()) {
                String lastMessage = (String) rawMeta.getOrDefault("lastMessage", "");
                String lastMessageAtStr = (String) rawMeta.getOrDefault("lastMessageAt", null);
                String lastMessageSeqIdStr = (String) rawMeta.getOrDefault("lastMessageSeqId", "0");

                LocalDateTime lastMessageAt = lastMessageAtStr != null
                        ? LocalDateTime.parse(lastMessageAtStr)
                        : LocalDateTime.MIN;

                ChatRoomMeta meta = new ChatRoomMeta(lastMessage, lastMessageAt, Long.parseLong(lastMessageSeqIdStr));
                result.put(code, meta);
            }
        }
        return result;
    }

}
