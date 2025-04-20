package com.sunsuwedding.chat.dto.room;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomSummaryResponse {

    private String chatRoomCode;
    private Long chatPartnerId;
    private String chatPartnerName;
    private String avatarUrl;
    private String lastMessage;
    private LocalDateTime lastMessageAt;
    private int unreadCount;
}