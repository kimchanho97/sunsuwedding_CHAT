package com.sunsuwedding.chat.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.support.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ControllerAdvice;

@Slf4j
@ControllerAdvice
public class StompExceptionHandler {

    @MessageExceptionHandler(MethodArgumentTypeMismatchException.class)
    public void handleHeaderTypeMismatch(MethodArgumentTypeMismatchException e) {
        log.warn("STOMP 헤더 타입 변환 실패: {}", e.getMessage());
        // 필요하면 클라이언트에 특정 에러 메시지 전송도 가능
    }

    @MessageExceptionHandler(Exception.class)
    public void handleOtherStompErrors(Exception e) {
        log.error("STOMP 처리 중 예외 발생", e);
        // 비즈니스 로직에 맞게 처리
    }
}
