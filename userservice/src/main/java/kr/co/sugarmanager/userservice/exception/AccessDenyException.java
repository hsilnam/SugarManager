package kr.co.sugarmanager.userservice.exception;

public class AccessDenyException extends CustomException {
    public AccessDenyException(ErrorCode errorCode) {
        super(errorCode);
    }
}
