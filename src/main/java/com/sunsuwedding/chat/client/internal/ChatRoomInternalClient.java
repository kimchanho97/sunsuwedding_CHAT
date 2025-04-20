package com.sunsuwedding.chat.client.internal;

import com.sunsuwedding.chat.common.exception.ChatErrorCode;
import com.sunsuwedding.chat.common.exception.CustomException;
import com.sunsuwedding.chat.dto.room.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

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

    public List<Long> getParticipantUserIds(String chatRoomCode) {
        String url = baseUrl + "/internal/chat/rooms/" + chatRoomCode + "/participants";
        try {
            ChatRoomParticipantsDto response = restTemplate.getForObject(url, ChatRoomParticipantsDto.class);
            return Optional.ofNullable(response)
                    .map(ChatRoomParticipantsDto::getParticipantUserIds)
                    .orElse(List.of());
        } catch (Exception e) {
            throw new CustomException(ChatErrorCode.CHAT_ROOM_PARTICIPANTS_FETCH_FAILED);
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
            throw new CustomException(ChatErrorCode.CHAT_ROOM_PARTNER_FETCH_FAILED);
        }
    }

}