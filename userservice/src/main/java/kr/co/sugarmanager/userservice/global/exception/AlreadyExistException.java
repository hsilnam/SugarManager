package kr.co.sugarmanager.userservice.global.exception;

public class AlreadyExistException extends CustomException {
    public AlreadyExistException(ErrorCode errorCode) {
        super(errorCode);
    }
}
