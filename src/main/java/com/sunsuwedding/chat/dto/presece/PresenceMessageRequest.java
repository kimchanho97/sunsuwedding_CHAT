package com.sunsuwedding.chat.dto.presece;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PresenceMessageRequest {

    @NotNull(message = "userId는 필수입니다.")
    private Long userId;

    @NotNull(message = "chatPartnerId는 필수입니다.")
    private Long chatPartnerId;
}
