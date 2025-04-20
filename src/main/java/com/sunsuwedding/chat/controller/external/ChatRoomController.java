package com.sunsuwedding.chat.controller.external;

import com.sunsuwedding.chat.common.response.ApiResponse;
import com.sunsuwedding.chat.dto.room.ChatRoomCreateRequest;
import com.sunsuwedding.chat.dto.room.ChatRoomCreateResponse;
import com.sunsuwedding.chat.dto.room.ChatRoomValidationRequest;
import com.sunsuwedding.chat.dto.room.ChatRoomValidationResponse;
import com.sunsuwedding.chat.service.ChatRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
