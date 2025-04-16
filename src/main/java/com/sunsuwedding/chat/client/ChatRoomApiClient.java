package com.sunsuwedding.chat.client;

import com.sunsuwedding.chat.common.exception.ChatErrorCode;
import com.sunsuwedding.chat.common.exception.CustomException;
import com.sunsuwedding.chat.dto.room.ChatRoomCreateRequest;
import com.sunsuwedding.chat.dto.room.ChatRoomCreateResponse;
import com.sunsuwedding.chat.dto.room.ChatRoomValidationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class ChatRoomApiClient {

    private final RestTemplate restTemplate;

    private final static String CHAT_ROOM_PATH = "/internal/chat-rooms";

    @Value("${backend.api.base-url}")
    private String baseUrl;


    public ChatRoomCreateResponse createOrFindChatRoom(ChatRoomCreateRequest request) {
        String url = baseUrl + CHAT_ROOM_PATH;
        try {
            return restTemplate.postForObject(url, request, ChatRoomCreateResponse.class);
        } catch (RestClientException e) {
            throw new CustomException(ChatErrorCode.CHAT_ROOM_API_FAILED);
        }
    }

    public boolean validateChatRoom(ChatRoomValidationRequest request) {
        String url = baseUrl + CHAT_ROOM_PATH + "/validate";
        try {
            return Boolean.TRUE.equals(restTemplate.postForObject(url, request, Boolean.class));
        } catch (RestClientException e) {
            throw new CustomException(ChatErrorCode.CHAT_ROOM_VALIDATION_FAILED);
        }
    }

}