package com.sunsuwedding.chat.controller.api;

import com.sunsuwedding.chat.client.ChatRoomApiClient;
import com.sunsuwedding.chat.common.response.ApiResponse;
import com.sunsuwedding.chat.dto.room.ChatRoomCreateRequest;
import com.sunsuwedding.chat.dto.room.ChatRoomCreateResponse;
import com.sunsuwedding.chat.dto.room.ChatRoomValidationRequest;
import com.sunsuwedding.chat.dto.room.ChatRoomValidationResponse;
import com.sunsuwedding.chat.redis.RedisChatRoomStore;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat-rooms")
public class ChatRoomExternalController {

    private final ChatRoomApiClient chatRoomApiClient;
    private final RedisChatRoomStore redisChatRoomStore;

    @PostMapping
    public ApiResponse<ChatRoomCreateResponse> createChatRoom(@RequestBody @Valid ChatRoomCreateRequest request) {
        // 1. 백엔드 서버에 채팅방 생성 요청
        ChatRoomCreateResponse response = chatRoomApiClient.createOrFindChatRoom(request);

        // 2. Redis에 채팅방 생성
        redisChatRoomStore.initializeChatRoomEntry(response.chatRoomCode(), request.userId(), request.plannerId());
        return ApiResponse.success(response);
    }

    @PostMapping("/validate")
    public ApiResponse<ChatRoomValidationResponse> validate(@RequestBody @Valid ChatRoomValidationRequest request) {
        // 1. Redis 먼저 조회
        boolean isInRedis = redisChatRoomStore.isUserInChatRoom(request.getChatRoomCode(), request.getUserId());
        if (isInRedis) {
            return ApiResponse.success(new ChatRoomValidationResponse(true));
        }

        // 2. Redis에 없을 경우, 백엔드로 유효성 검증 요청
        boolean isValid = chatRoomApiClient.validateChatRoom(request);
        return ApiResponse.success(new ChatRoomValidationResponse(isValid));
    }

}
