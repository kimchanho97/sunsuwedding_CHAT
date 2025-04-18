package com.sunsuwedding.chat.service;

import com.sunsuwedding.chat.client.ChatRoomApiClient;
import com.sunsuwedding.chat.dto.room.ChatRoomCreateRequest;
import com.sunsuwedding.chat.dto.room.ChatRoomCreateResponse;
import com.sunsuwedding.chat.dto.room.ChatRoomValidationRequest;
import com.sunsuwedding.chat.redis.RedisChatReadStore;
import com.sunsuwedding.chat.redis.RedisChatRoomStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomApiClient chatRoomApiClient;
    private final RedisChatRoomStore redisChatRoomStore;
    private final RedisChatReadStore redisChatReadStore;

    public ChatRoomCreateResponse createChatRoom(ChatRoomCreateRequest request) {
        // RDB에 채팅방 생성 요청
        ChatRoomCreateResponse response = chatRoomApiClient.createOrFindChatRoom(request);

        // Redis 등록
        redisChatRoomStore.addChatRoomToUser(request.userId(), response.chatRoomCode());
        redisChatRoomStore.addChatRoomToUser(request.plannerId(), response.chatRoomCode());

        redisChatRoomStore.addMemberToChatRoom(response.chatRoomCode(), request.userId());
        redisChatRoomStore.addMemberToChatRoom(response.chatRoomCode(), request.plannerId());

        redisChatReadStore.initializeLastReadSequence(response.chatRoomCode(), request.userId());
        redisChatReadStore.initializeLastReadSequence(response.chatRoomCode(), request.plannerId());
        return response;
    }

    public boolean validateChatRoom(ChatRoomValidationRequest request) {
        if (redisChatRoomStore.isMemberOfChatRoom(request.getChatRoomCode(), request.getUserId())) {
            return true;
        }
        return chatRoomApiClient.validateChatRoom(request);
    }
}
