package com.sunsuwedding.chat.controller.api;

import com.sunsuwedding.chat.common.response.PaginationResponse;
import com.sunsuwedding.chat.dto.message.ChatMessageResponse;
import com.sunsuwedding.chat.service.ChatMessageQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat-messages")
public class ChatMessageQueryController {

    private final ChatMessageQueryService chatMessageQueryService;

    @GetMapping("/{chatRoomCode}")
    public PaginationResponse<ChatMessageResponse> getMessages(
            @PathVariable String chatRoomCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size) {
        return chatMessageQueryService.getMessages(chatRoomCode, page, size);
    }

}
