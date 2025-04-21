package com.sunsuwedding.chat.client.internal;

import com.sunsuwedding.chat.common.exception.ChatErrorCode;
import com.sunsuwedding.chat.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ChatMessageReadClient {

    @Value("${backend.api.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    public Map<Long, Long> getReadSequencesByUserInChatRoom(String chatRoomCode) {
        String url = baseUrl + "/internal/chat/rooms/" + chatRoomCode + "/last-read-sequences";
        try {
            ParameterizedTypeReference<Map<Long, Long>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<Map<Long, Long>> response = restTemplate.exchange(url, HttpMethod.GET, null, responseType);
            return response.getBody() != null ? response.getBody() : Map.of();
        } catch (Exception e) {
            throw new CustomException(ChatErrorCode.USER_LAST_READ_FETCH_FAILED);
        }
    }
}
