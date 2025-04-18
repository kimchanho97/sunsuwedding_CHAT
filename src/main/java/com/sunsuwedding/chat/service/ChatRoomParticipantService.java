package com.sunsuwedding.chat.service;

import com.sunsuwedding.chat.client.ChatRoomApiClient;
import com.sunsuwedding.chat.redis.RedisChatRoomStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ChatRoomParticipantService {

    private final ChatRoomApiClient chatRoomApiClient;
    private final RedisChatRoomStore redisChatRoomStore;

    public List<Long> getParticipantUserIds(String chatRoomCode) {
        Set<String> members = redisChatRoomStore.getChatRoomMembers(chatRoomCode);
        if (members != null && !members.isEmpty()) {
            return members.stream()
                    .map(Long::parseLong)
                    .toList();
        }

        // Redis에 없을 경우 → RDB 조회
        List<Long> userIds = chatRoomApiClient.getParticipantUserIds(chatRoomCode);

        // Redis 캐시 갱신
        redisChatRoomStore.addMembersToChatRoom(chatRoomCode, userIds);
        return userIds;
    }
}
