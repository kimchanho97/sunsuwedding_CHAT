package com.sunsuwedding.chat.service;

import com.sunsuwedding.chat.client.internal.ChatImageUploadClient;
import com.sunsuwedding.chat.common.response.PaginationResponse;
import com.sunsuwedding.chat.domain.ChatMessageDocument;
import com.sunsuwedding.chat.dto.message.ChatMessageRequest;
import com.sunsuwedding.chat.dto.message.ChatMessageResponse;
import com.sunsuwedding.chat.dto.message.S3UploadResultDto;
import com.sunsuwedding.chat.event.ChatMessageRequestEvent;
import com.sunsuwedding.chat.kafka.producer.ChatMessageProducer;
import com.sunsuwedding.chat.redis.RedisChatReadStore;
import com.sunsuwedding.chat.repository.ChatMessageMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageMongoRepository repository;
    private final RedisChatReadStore redisChatReadStore;
    private final ChatRoomParticipantService chatRoomParticipantService;
    private final ChatImageUploadClient chatImageUploadClient;
    private final ChatMessageProducer chatMessageProducer;

    public PaginationResponse<ChatMessageResponse> getMessages(String chatRoomCode, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Slice<ChatMessageDocument> slice = repository.findByChatRoomCodeOrderByCreatedAtDesc(chatRoomCode, pageable);

        List<Long> participantIds = chatRoomParticipantService.getParticipantUserIds(chatRoomCode);
        Map<Long, Long> userReadSeqMap = redisChatReadStore.getUserReadSequences(chatRoomCode, participantIds);

        List<ChatMessageResponse> responses = slice.getContent().stream()
                .map(doc -> ChatMessageResponse.from(doc, userReadSeqMap))
                .toList();

        return new PaginationResponse<>(responses, slice.hasNext());
    }

    public void uploadImageAndSend(String chatRoomCode, ChatMessageRequest message, MultipartFile imageFile) {
        // 1. 백엔드에 이미지 업로드 요청
        S3UploadResultDto uploadResult = chatImageUploadClient.uploadImage(imageFile);

        // 2. 업로드 결과를 포함한 메시지 이벤트 생성
        chatMessageProducer.send(ChatMessageRequestEvent.from(message, chatRoomCode, uploadResult));
    }
}
