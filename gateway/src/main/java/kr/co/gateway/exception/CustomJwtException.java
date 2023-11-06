package kr.co.gateway.exception;

public class CustomJwtException extends CustomException {
    public CustomJwtException(ErrorCode errorCode) {
        super(errorCode);
    }
}