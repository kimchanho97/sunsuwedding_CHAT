package com.sunsuwedding.chat.service;

import com.sunsuwedding.chat.client.internal.ChatRoomInternalClient;
import com.sunsuwedding.chat.common.response.PaginationResponse;
import com.sunsuwedding.chat.dto.room.*;
import com.sunsuwedding.chat.model.ChatRoomMeta;
import com.sunsuwedding.chat.redis.RedisChatReadStore;
import com.sunsuwedding.chat.redis.RedisChatRoomStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomInternalClient chatRoomInternalClient;
    private final RedisChatRoomStore redisChatRoomStore;
    private final RedisChatReadStore redisChatReadStore;
    private final ChatRoomQueryService chatRoomQueryService;
    private final ChatMessageReadQueryService chatMessageReadQueryService;

    public ChatRoomCreateResponse createChatRoom(ChatRoomCreateRequest request) {
        // RDB에 채팅방 생성 요청
        ChatRoomCreateResponse response = chatRoomInternalClient.createOrFindChatRoom(request);

        // Redis 최신화 (ZSET + SET은 중복 걱정 없음)
        redisChatRoomStore.addChatRoomToUser(request.userId(), response.chatRoomCode());
        redisChatRoomStore.addChatRoomToUser(request.plannerId(), response.chatRoomCode());
        redisChatRoomStore.addMemberToChatRoom(response.chatRoomCode(), request.userId());
        redisChatRoomStore.addMemberToChatRoom(response.chatRoomCode(), request.plannerId());

        // 초기화는 새로 생성된 경우만
        if (!response.alreadyExists()) {
            redisChatReadStore.initializeLastReadSequence(response.chatRoomCode(), request.userId());
            redisChatReadStore.initializeLastReadSequence(response.chatRoomCode(), request.plannerId());
            redisChatRoomStore.initializeChatRoomMeta(response.chatRoomCode());
        }
        return response;
    }

    public boolean validateChatRoom(ChatRoomValidationRequest request) {
        if (redisChatRoomStore.isMemberOfChatRoom(request.getChatRoomCode(), request.getUserId())) {
            return true;
        }
        return chatRoomInternalClient.validateChatRoom(request);
    }

    public PaginationResponse<ChatRoomSummaryResponse> getChatRooms(Long userId, int size) {
        // 1. 채팅방 목록, 메타, 상대방 정보, 읽은 시퀀스 조회
        List<String> chatRoomCodes = chatRoomQueryService.getSortedChatRoomCodes(userId, size);
        long totalCount = chatRoomQueryService.countChatRooms(userId);
        Map<String, ChatRoomMeta> chatRoomMetas = chatRoomQueryService.getChatRoomMetas(chatRoomCodes);
        Map<String, ChatRoomPartnerProfileResponse> partnerProfileMap = getPartnerProfileMap(chatRoomCodes, userId);
        Map<String, Long> lastReadSeqMap = chatMessageReadQueryService.getReadSequencesByChatRoomsForUser(chatRoomCodes, userId);

        // 2. 응답 조립
        List<ChatRoomSummaryResponse> responseList = buildSummaryResponses(chatRoomCodes, chatRoomMetas, partnerProfileMap, lastReadSeqMap);

        boolean hasNext = size < totalCount;
        return new PaginationResponse<>(responseList, hasNext);
    }

    private Map<String, ChatRoomPartnerProfileResponse> getPartnerProfileMap(List<String> chatRoomCodes, Long userId) {
        return chatRoomInternalClient.getPartnerProfiles(chatRoomCodes, userId).stream()
                .collect(Collectors.toMap(ChatRoomPartnerProfileResponse::chatRoomCode, Function.identity()));
    }

    private List<ChatRoomSummaryResponse> buildSummaryResponses(
            List<String> chatRoomCodes,
            Map<String, ChatRoomMeta> chatRoomMetas,
            Map<String, ChatRoomPartnerProfileResponse> partnerProfileMap,
            Map<String, Long> lastReadSeqMap
    ) {
        return chatRoomCodes.stream()
                .map(code -> ChatRoomSummaryResponse.from(
                        code,
                        partnerProfileMap.get(code),
                        chatRoomMetas.get(code),
                        lastReadSeqMap.get(code)
                ))
                .toList();
    }

}
