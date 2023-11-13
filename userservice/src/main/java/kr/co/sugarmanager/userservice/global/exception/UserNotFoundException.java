package kr.co.sugarmanager.userservice.global.exception;

public class UserNotFoundException extends CustomException{
    public UserNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
