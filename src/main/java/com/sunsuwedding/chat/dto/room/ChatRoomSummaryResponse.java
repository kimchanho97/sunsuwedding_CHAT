package com.sunsuwedding.chat.dto.room;

import com.sunsuwedding.chat.model.ChatRoomMeta;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;

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
        LocalDateTime lastMessageAtKST = meta.lastMessageAt()
                .atZone(ZoneId.of("UTC"))
                .withZoneSameInstant(ZoneId.of("Asia/Seoul"))
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
