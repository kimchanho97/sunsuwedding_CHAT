package com.sunsuwedding.chat.dto.room;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomParticipantsDto {
    private List<Long> participantUserIds;
}
