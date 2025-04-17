package com.sunsuwedding.chat.dto.presence;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PresencePingMessageRequest {

    @NotNull(message = "userId는 필수입니다.")
    private Long userId;
}
