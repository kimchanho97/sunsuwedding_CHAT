package com.sunsuwedding.chat.common.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCode {

    HttpStatus getHttpStatus();

    int getCode();

    String getMessage();
}
