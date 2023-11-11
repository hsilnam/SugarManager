package kr.co.sugarmanager.search.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;

    protected CustomException(ErrorCode errorCode) {
        this.errorCode=errorCode;
    }
}

