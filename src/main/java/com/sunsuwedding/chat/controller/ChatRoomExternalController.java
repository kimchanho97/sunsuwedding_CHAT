package com.sunsuwedding.chat.controller;

import com.sunsuwedding.chat.client.ChatRoomApiClient;
import com.sunsuwedding.chat.common.response.ApiResponse;
import com.sunsuwedding.chat.dto.room.ChatRoomCreateRequest;
import com.sunsuwedding.chat.dto.room.ChatRoomCreateResponse;
import com.sunsuwedding.chat.dto.room.ChatRoomValidationRequest;
import com.sunsuwedding.chat.dto.room.ChatRoomValidationResponse;
import com.sunsuwedding.chat.redis.RedisChatRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat-rooms")
public class ChatRoomExternalController {

    private final ChatRoomApiClient chatRoomApiClient;
    private final RedisChatRoomService redisChatRoomService;

    @PostMapping
    public ResponseEntity<ApiResponse<ChatRoomCreateResponse>> createChatRoom(
            @RequestBody @Valid ChatRoomCreateRequest request
    ) {
        // 1. 백엔드 서버에 채팅방 생성 요청
        ChatRoomCreateResponse response = chatRoomApiClient.createOrFindChatRoom(request);

        // 2. Redis 메타 저장
        redisChatRoomService.saveChatRoomMeta(response.chatRoomId(), request.userId(), request.plannerId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<ChatRoomValidationResponse>> validate(@RequestBody @Valid ChatRoomValidationRequest request) {

        boolean isValid = chatRoomApiClient.validateChatRoom(request);
        ChatRoomValidationResponse response = new ChatRoomValidationResponse(isValid);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

}