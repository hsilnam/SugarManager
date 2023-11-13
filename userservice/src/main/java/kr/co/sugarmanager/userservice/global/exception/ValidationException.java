package kr.co.sugarmanager.userservice.global.exception;

public class ValidationException extends CustomException {
    public ValidationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
