package kr.co.gateway.exception;

public class InternalServerErrorException extends CustomException {
    public InternalServerErrorException(ErrorCode errorCode) {
        super(errorCode);
    }
}
