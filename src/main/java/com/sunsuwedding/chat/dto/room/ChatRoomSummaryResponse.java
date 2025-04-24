package com.sunsuwedding.chat.dto.room;

import com.sunsuwedding.chat.model.ChatRoomMeta;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
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

    public static ChatRoomSummaryResponse from(
            String chatRoomCode,
            ChatRoomPartnerProfileResponse partner,
            ChatRoomMeta meta,
            Long readSeq
    ) {
        int unread = (readSeq == null) ? 0 : (int) (meta.lastMessageSeqId() - readSeq);
        ZoneId utc = ZoneId.of("UTC");
        ZoneId kst = ZoneId.of("Asia/Seoul");

        LocalDateTime lastMessageAtUTC = meta.lastMessageAt();
        LocalDateTime lastMessageAtKST = lastMessageAtUTC.atZone(utc)
                .withZoneSameInstant(kst)
                .toLocalDateTime();

        return new ChatRoomSummaryResponse(
                chatRoomCode,
                partner.partnerUserId(),
                partner.partnerName(),
                partner.avatarUrl(),
                meta.lastMessage(),
                lastMessageAtKST,
                Math.max(unread, 0)
        );
    }
}
