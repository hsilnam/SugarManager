package kr.co.sugarmanager.userservice.global.exception;

public class CustomJwtException extends CustomException{
    public CustomJwtException(ErrorCode errorCode) {
        super(errorCode);
    }
}