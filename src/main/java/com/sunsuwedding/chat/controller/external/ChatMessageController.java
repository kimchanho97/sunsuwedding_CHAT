package com.sunsuwedding.chat.controller.external;

import com.sunsuwedding.chat.common.response.ApiResponse;
import com.sunsuwedding.chat.common.response.PaginationResponse;
import com.sunsuwedding.chat.dto.message.ChatMessageRequest;
import com.sunsuwedding.chat.dto.message.ChatMessageResponse;
import com.sunsuwedding.chat.service.ChatMessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat/messages")
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    @GetMapping("/{chatRoomCode}")
    public PaginationResponse<ChatMessageResponse> getMessages(
            @PathVariable String chatRoomCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size) {
        return chatMessageService.getMessages(chatRoomCode, page, size);
    }

    @PostMapping(value = "/{chatRoomCode}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Void> sendImageMessage(
            @PathVariable String chatRoomCode,
            @RequestPart("message") @Valid ChatMessageRequest request,
            @RequestPart("image") MultipartFile imageFile
    ) {
        chatMessageService.uploadImageAndSend(chatRoomCode, request, imageFile);
        return ApiResponse.success(null);
    }

}
