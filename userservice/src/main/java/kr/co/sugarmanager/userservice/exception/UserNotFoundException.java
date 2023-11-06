package kr.co.sugarmanager.userservice.exception;

public class UserNotFoundException extends CustomException{
    public UserNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
