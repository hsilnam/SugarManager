package kr.co.gateway.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final kr.co.gateway.exception.ErrorCode errorCode;

    public CustomException(kr.co.gateway.exception.ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
