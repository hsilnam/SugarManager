package kr.co.sugarmanager.userservice.exception;

public class CustomJwtException extends CustomException{
    public CustomJwtException(ErrorCode errorCode) {
        super(errorCode);
    }
}