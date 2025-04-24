package com.sunsuwedding.chat.client.internal;

import com.sunsuwedding.chat.common.exception.CustomException;
import com.sunsuwedding.chat.model.ChatRoomMeta;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static com.sunsuwedding.chat.common.exception.ChatErrorCode.CHAT_ROOM_META_FETCH_FAILED;

@Component
@RequiredArgsConstructor
public class ChatRoomMetaClient {

    private final RestTemplate restTemplate;

    @Value("${backend.api.base-url}")
    private String baseUrl;

    private static final String CHAT_ROOM_META_PATH = "/internal/chat/rooms/meta";

    public Map<String, ChatRoomMeta> getChatRoomMetas(List<String> chatRoomCodes) {
        String url = baseUrl + CHAT_ROOM_META_PATH;

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<List<String>> entity = new HttpEntity<>(chatRoomCodes, headers);

            ParameterizedTypeReference<Map<String, ChatRoomMeta>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<Map<String, ChatRoomMeta>> response =
                    restTemplate.exchange(url, HttpMethod.POST, entity, responseType);

            return response.getBody() != null ? response.getBody() : Map.of();
        } catch (Exception e) {
            throw new CustomException(CHAT_ROOM_META_FETCH_FAILED);
        }
    }

    public Map<String, ChatRoomMeta> getAllChatRoomMetas() {
        String url = baseUrl + CHAT_ROOM_META_PATH + "/all";

        try {
            ParameterizedTypeReference<Map<String, ChatRoomMeta>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<Map<String, ChatRoomMeta>> response =
                    restTemplate.exchange(url, HttpMethod.GET, null, responseType);

            return response.getBody() != null ? response.getBody() : Map.of();
        } catch (Exception e) {
            throw new CustomException(CHAT_ROOM_META_FETCH_FAILED);
        }
    }

}
