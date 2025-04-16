package com.sunsuwedding.chat.common.response;

import com.sunsuwedding.chat.common.exception.CustomException;
import com.sunsuwedding.chat.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public class ErrorResponse {

    private final int code;
    private final String message;

    public ErrorResponse(CustomException e) {
        this.code = e.getCode();
        this.message = e.getMessage();
    }

    public ErrorResponse(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    public ErrorResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
