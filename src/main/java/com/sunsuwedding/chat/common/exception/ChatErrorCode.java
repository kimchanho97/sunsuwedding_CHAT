package com.sunsuwedding.chat.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ChatErrorCode implements ErrorCode {

    // 채팅방 관련 에러
    CHAT_ROOM_API_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "채팅방 생성에 실패했습니다."),
    CHAT_ROOM_VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "채팅방 유효성 검사에 실패했습니다."),

    // 이미지 업로드 관련 에러
    IMAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드에 실패했습니다."),
    IMAGE_CONVERT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 변환에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public int getCode() {
        return httpStatus.value();
    }
}
