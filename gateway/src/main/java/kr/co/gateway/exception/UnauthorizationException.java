package kr.co.gateway.exception;

public class UnauthorizationException extends CustomException {
    public UnauthorizationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
