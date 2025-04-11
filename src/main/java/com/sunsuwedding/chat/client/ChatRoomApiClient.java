package com.sunsuwedding.chat.client;

import com.sunsuwedding.chat.dto.room.ChatRoomCreateRequest;
import com.sunsuwedding.chat.dto.room.ChatRoomCreateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class ChatRoomApiClient {

    private final RestTemplate restTemplate;

    @Value("${backend.api.base-url}")
    private String baseUrl;

    @Value("${backend.api.chat-room-path}")
    private String chatRoomPath;

    public ChatRoomCreateResponse createOrFindChatRoom(ChatRoomCreateRequest request) {
        String url = baseUrl + chatRoomPath;
        return restTemplate.postForObject(url, request, ChatRoomCreateResponse.class);
    }
}