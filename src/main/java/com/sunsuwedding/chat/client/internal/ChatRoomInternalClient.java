package com.sunsuwedding.chat.client.internal;

import com.sunsuwedding.chat.common.exception.CustomException;
import com.sunsuwedding.chat.dto.room.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.sunsuwedding.chat.common.exception.ChatErrorCode.*;

@Component
@RequiredArgsConstructor
public class ChatRoomInternalClient {

    private final RestTemplate restTemplate;

    private final static String CHAT_ROOM_PATH = "/internal/chat/rooms";

    @Value("${backend.api.base-url}")
    private String baseUrl;

    public ChatRoomCreateResponse createOrFindChatRoom(ChatRoomCreateRequest request) {
        String url = baseUrl + CHAT_ROOM_PATH;
        try {
            return restTemplate.postForObject(url, request, ChatRoomCreateResponse.class);
        } catch (RestClientException e) {
            throw new CustomException(CHAT_ROOM_API_FAILED);
        }
    }

    public boolean validateChatRoom(ChatRoomValidationRequest request) {
        String url = baseUrl + CHAT_ROOM_PATH + "/validate";
        try {
            return Boolean.TRUE.equals(restTemplate.postForObject(url, request, Boolean.class));
        } catch (RestClientException e) {
            throw new CustomException(CHAT_ROOM_VALIDATION_FAILED);
        }
    }

    public List<Long> getParticipantUserIds(String chatRoomCode) {
        String url = baseUrl + "/internal/chat/rooms/" + chatRoomCode + "/participants";
        try {
            ChatRoomParticipantsDto response = restTemplate.getForObject(url, ChatRoomParticipantsDto.class);
            return Optional.ofNullable(response)
                    .map(ChatRoomParticipantsDto::getParticipantUserIds)
                    .orElse(List.of());
        } catch (Exception e) {
            throw new CustomException(CHAT_ROOM_PARTICIPANTS_FETCH_FAILED);
        }
    }

    public List<ChatRoomPartnerProfileResponse> getPartnerProfiles(List<String> chatRoomCodes, Long userId) {
        String url = baseUrl + CHAT_ROOM_PATH + "/partners";
        ChatRoomPartnerProfileRequest request = new ChatRoomPartnerProfileRequest(userId, chatRoomCodes);
        try {
            ChatRoomPartnerProfileResponse[] response = restTemplate.postForObject(
                    url,
                    request,
                    ChatRoomPartnerProfileResponse[].class
            );

            return response != null ? List.of(response) : List.of();
        } catch (RestClientException e) {
            throw new CustomException(CHAT_ROOM_PARTNER_FETCH_FAILED);
        }
    }

    public List<String> getSortedChatRoomCodes(Long userId, int size) {
        String url = baseUrl + CHAT_ROOM_PATH + "/sorted?userId=" + userId + "&size=" + size;
        try {
            ParameterizedTypeReference<List<String>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<List<String>> response = restTemplate.exchange(url, HttpMethod.GET, null, responseType);
            return response.getBody() != null ? response.getBody() : List.of();
        } catch (Exception e) {
            throw new CustomException(SORTED_CHAT_ROOM_CODES_FETCH_FAILED);
        }
    }

    public long countChatRooms(Long userId) {
        String url = baseUrl + CHAT_ROOM_PATH + "/count?userId=" + userId;
        try {
            ResponseEntity<Long> response = restTemplate.getForEntity(url, Long.class);
            return response.getBody() != null ? response.getBody() : 0L;
        } catch (Exception e) {
            throw new CustomException(CHAT_ROOM_COUNT_FETCH_FAILED);
        }
    }

    public Map<String, Long> getReadSequencesByChatRoomsForUser(List<String> chatRoomCodes, Long userId) {
        String url = baseUrl + CHAT_ROOM_PATH + "/last-read-sequences";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 요청 객체 생성
            Map<String, Object> body = Map.of(
                    "chatRoomCodes", chatRoomCodes,
                    "userId", userId
            );
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            ParameterizedTypeReference<Map<String, Long>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<Map<String, Long>> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, responseType);

            return response.getBody() != null ? response.getBody() : Map.of();
        } catch (Exception e) {
            throw new CustomException(LAST_READ_SEQUENCE_FETCH_FAILED);
        }
    }

}