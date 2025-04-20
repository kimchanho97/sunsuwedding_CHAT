package com.sunsuwedding.chat.controller.external;

import com.sunsuwedding.chat.common.response.ApiResponse;
import com.sunsuwedding.chat.common.response.PaginationResponse;
import com.sunsuwedding.chat.dto.room.*;
import com.sunsuwedding.chat.service.ChatRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat/rooms")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping
    public ApiResponse<ChatRoomCreateResponse> createChatRoom(@RequestBody @Valid ChatRoomCreateRequest request) {
        return ApiResponse.success(chatRoomService.createChatRoom(request));
    }

    @PostMapping("/validate")
    public ApiResponse<ChatRoomValidationResponse> validate(@RequestBody @Valid ChatRoomValidationRequest request) {
        boolean isValid = chatRoomService.validateChatRoom(request);
        return ApiResponse.success(new ChatRoomValidationResponse(isValid));
    }

    @GetMapping
    public PaginationResponse<ChatRoomSummaryResponse> getChatRooms(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "10") int size
    ) {
        return chatRoomService.getChatRooms(userId, size);
    }

}
