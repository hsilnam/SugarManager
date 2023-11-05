package kr.co.sugarmanager.userservice.exception;

public class ValidationException extends CustomException {
    public ValidationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
