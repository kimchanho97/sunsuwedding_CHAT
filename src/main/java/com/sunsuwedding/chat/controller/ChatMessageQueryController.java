package com.sunsuwedding.chat.controller;

import com.sunsuwedding.chat.domain.ChatMessageDocument;
import com.sunsuwedding.chat.dto.ChatMessageListResponse;
import com.sunsuwedding.chat.repository.ChatMessageMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat-messages")
public class ChatMessageQueryController {

    private final ChatMessageMongoRepository repository;

    @GetMapping("/{chatRoomId}")
    public ResponseEntity<ChatMessageListResponse> getMessages(
            @PathVariable Long chatRoomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size
    ) {

        log.info("💬 채팅 메시지 조회 요청: roomId={}, page={}, size={}", chatRoomId, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Slice<ChatMessageDocument> slice = repository.findByChatRoomIdOrderByCreatedAtDesc(chatRoomId, pageable);

        log.info("📦 메시지 수: {}, hasNext={}", slice.getNumberOfElements(), slice.hasNext());

        ChatMessageListResponse response = ChatMessageListResponse.from(slice);
        return ResponseEntity.ok(response);
    }

}
