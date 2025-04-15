package com.sunsuwedding.chat.controller.api;

import com.sunsuwedding.chat.domain.ChatMessageDocument;
import com.sunsuwedding.chat.dto.message.ChatMessageListResponse;
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

    @GetMapping("/{chatRoomCode}")
    public ResponseEntity<ChatMessageListResponse> getMessages(
            @PathVariable String chatRoomCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size
    ) {

        log.info("💬 채팅 메시지 조회 요청: roomId={}, page={}, size={}", chatRoomCode, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Slice<ChatMessageDocument> slice = repository.findByChatRoomCodeOrderByCreatedAtDesc(chatRoomCode, pageable);

        log.info("📦 메시지 수: {}, hasNext={}", slice.getNumberOfElements(), slice.hasNext());

        ChatMessageListResponse response = ChatMessageListResponse.from(slice);
        return ResponseEntity.ok(response);
    }

}
