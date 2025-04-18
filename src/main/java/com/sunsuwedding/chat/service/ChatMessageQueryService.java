package com.sunsuwedding.chat.service;

import com.sunsuwedding.chat.client.ChatRoomApiClient;
import com.sunsuwedding.chat.common.response.PaginationResponse;
import com.sunsuwedding.chat.domain.ChatMessageDocument;
import com.sunsuwedding.chat.dto.message.ChatMessageResponse;
import com.sunsuwedding.chat.redis.RedisChatReadStore;
import com.sunsuwedding.chat.repository.ChatMessageMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatMessageQueryService {

    private final ChatMessageMongoRepository repository;
    private final ChatRoomApiClient chatRoomApiClient;
    private final RedisChatReadStore redisChatReadStore;

    public PaginationResponse<ChatMessageResponse> getMessages(String chatRoomCode, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Slice<ChatMessageDocument> slice = repository.findByChatRoomCodeOrderByCreatedAtDesc(chatRoomCode, pageable);

        List<Long> participantIds = chatRoomApiClient.getParticipantUserIds(chatRoomCode);
        Map<Long, Long> userReadSeqMap = redisChatReadStore.getUserReadSequences(chatRoomCode, participantIds);

        List<ChatMessageResponse> responses = slice.getContent().stream()
                .map(doc -> ChatMessageResponse.from(doc, userReadSeqMap))
                .toList();

        return new PaginationResponse<>(responses, slice.hasNext());
    }
}
