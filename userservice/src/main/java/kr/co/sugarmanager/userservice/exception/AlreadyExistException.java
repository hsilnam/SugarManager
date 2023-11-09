package kr.co.sugarmanager.userservice.exception;

public class AlreadyExistException extends CustomException {
    public AlreadyExistException(ErrorCode errorCode) {
        super(errorCode);
    }
}
