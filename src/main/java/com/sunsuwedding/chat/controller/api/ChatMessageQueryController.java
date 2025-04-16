package com.sunsuwedding.chat.controller.api;

import com.sunsuwedding.chat.common.response.ApiResponse;
import com.sunsuwedding.chat.domain.ChatMessageDocument;
import com.sunsuwedding.chat.dto.message.ChatMessageListResponse;
import com.sunsuwedding.chat.repository.ChatMessageMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat-messages")
public class ChatMessageQueryController {

    private final ChatMessageMongoRepository repository;

    @GetMapping("/{chatRoomCode}")
    public ApiResponse<ChatMessageListResponse> getMessages(@PathVariable String chatRoomCode,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "30") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Slice<ChatMessageDocument> slice = repository.findByChatRoomCodeOrderByCreatedAtDesc(chatRoomCode, pageable);
        return ApiResponse.success(ChatMessageListResponse.from(slice));
    }

}
