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
    CHAT_ROOM_PARTICIPANTS_FETCH_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "채팅방 참여자 조회에 실패했습니다."),
    CHAT_ROOM_PARTNER_FETCH_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "채팅방 상대방 정보 조회에 실패했습니다."),
    USER_LAST_READ_FETCH_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "사용자 마지막 읽기 시퀀스 조회에 실패했습니다."),
    SORTED_CHAT_ROOM_CODES_FETCH_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "정렬된 채팅방 코드 조회에 실패했습니다."),
    CHAT_ROOM_COUNT_FETCH_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "채팅방 개수 조회에 실패했습니다."),
    CHAT_ROOM_META_FETCH_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "채팅방 메타 정보 조회에 실패했습니다."),
    LAST_READ_SEQUENCE_FETCH_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "마지막 읽기 시퀀스 조회에 실패했습니다."),

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
