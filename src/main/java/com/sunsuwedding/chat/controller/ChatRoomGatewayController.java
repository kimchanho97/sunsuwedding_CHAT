package com.sunsuwedding.chat.controller;

import com.sunsuwedding.chat.client.ChatRoomApiClient;
import com.sunsuwedding.chat.common.response.ApiResponse;
import com.sunsuwedding.chat.dto.ChatRoomCreateRequest;
import com.sunsuwedding.chat.dto.ChatRoomCreateResponse;
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
public class ChatRoomGatewayController {

    private final ChatRoomApiClient chatRoomApiClient;
    private final RedisChatRoomService redisChatRoomService;

    @PostMapping
    public ResponseEntity<ApiResponse<ChatRoomCreateResponse>> createChatRoom(
            @RequestBody @Valid ChatRoomCreateRequest request
    ) {
        // 1. 백엔드 서버에 채팅방 생성 요청
        ChatRoomCreateResponse response = chatRoomApiClient.createOrFindChatRoom(request);

        // 2. Redis 메타 저장
        redisChatRoomService.saveChatRoomMeta(
                response.chatRoomId(),
                request.userId(),
                request.plannerId()
        );

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}