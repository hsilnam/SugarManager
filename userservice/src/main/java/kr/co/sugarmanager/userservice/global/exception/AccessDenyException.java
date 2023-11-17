package kr.co.sugarmanager.userservice.global.exception;

public class AccessDenyException extends CustomException {
    public AccessDenyException(ErrorCode errorCode) {
        super(errorCode);
    }
}
