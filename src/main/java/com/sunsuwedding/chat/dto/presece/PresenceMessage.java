package com.sunsuwedding.chat.dto.presece;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PresenceMessage {
    private Long userId;
    private Long chatPartnerId;
}
