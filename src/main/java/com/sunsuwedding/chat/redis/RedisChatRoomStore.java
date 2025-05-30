package com.sunsuwedding.chat.redis;

import com.sunsuwedding.chat.model.ChatRoomMeta;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RedisChatRoomStore {

    private final RedisTemplate<String, String> redisTemplate;

    public void addChatRoomToUser(Long userId, String chatRoomCode) {
        long now = System.currentTimeMillis();
        redisTemplate.opsForZSet().add(RedisKeyUtil.userChatRoomsKey(userId), chatRoomCode, now);
    }

    // 정렬 유지용 (lastMessageAt 기반)
    public void addChatRoomWithScore(Long userId, String chatRoomCode, long score) {
        redisTemplate.opsForZSet().add(RedisKeyUtil.userChatRoomsKey(userId), chatRoomCode, score);
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

    public void setMessageSeq(String chatRoomCode, Long nextSeqId) {
        String key = RedisKeyUtil.chatRoomMessageSeqKey(chatRoomCode);
        redisTemplate.opsForValue().set(key, String.valueOf(nextSeqId));
    }

    public void initializeChatRoomMeta(String chatRoomCode) {
        String key = RedisKeyUtil.chatRoomMetaKey(chatRoomCode);
        Map<String, String> meta = Map.of(
                "lastMessage", "",
                "lastMessageAt", LocalDateTime.now().toString(),
                "lastMessageSeqId", "0"
        );
        redisTemplate.opsForHash().putAll(key, meta);
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

            String lastMessage = (String) rawMeta.get("lastMessage");
            String lastMessageAtStr = (String) rawMeta.get("lastMessageAt");
            String lastMessageSeqIdStr = (String) rawMeta.get("lastMessageSeqId");

            ChatRoomMeta meta = new ChatRoomMeta(
                    lastMessage,
                    LocalDateTime.parse(lastMessageAtStr),
                    Long.parseLong(lastMessageSeqIdStr)
            );
            result.put(code, meta);
        }
        return result;
    }

    public boolean existsChatRoomMeta(String chatRoomCode) {
        String key = RedisKeyUtil.chatRoomMetaKey(chatRoomCode);
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void markChatRoomMetaAsDirty(String chatRoomCode) {
        String dirtyKey = RedisKeyUtil.dirtyChatRoomMetaKey(); // dirty:chat:room:meta
        redisTemplate.opsForSet().add(dirtyKey, chatRoomCode);
    }

    public Set<String> getDirtyChatRoomMetaCodes() {
        return redisTemplate.opsForSet().members(RedisKeyUtil.dirtyChatRoomMetaKey());
    }

    public Map<String, String> getChatRoomMeta(String chatRoomCode) {
        String key = RedisKeyUtil.chatRoomMetaKey(chatRoomCode);
        Map<Object, Object> raw = redisTemplate.opsForHash().entries(key);
        return raw.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().toString(),
                        e -> e.getValue() != null ? e.getValue().toString() : ""
                ));
    }

    public void removeDirtyChatRoomMetaCodes(Collection<String> chatRoomCodes) {
        redisTemplate.opsForSet().remove(RedisKeyUtil.dirtyChatRoomMetaKey(), chatRoomCodes.toArray(new Object[0]));
    }

}
