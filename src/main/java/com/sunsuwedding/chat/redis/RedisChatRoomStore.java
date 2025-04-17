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

    public Long nextMessageSeq(String chatRoomCode) {
        String key = RedisKeyUtil.chatRoomMessageSeqKey(chatRoomCode);
        return redisTemplate.opsForValue().increment(key);
    }

}
