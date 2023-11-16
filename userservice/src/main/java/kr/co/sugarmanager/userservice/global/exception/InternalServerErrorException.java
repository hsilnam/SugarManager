package kr.co.sugarmanager.userservice.global.exception;

public class InternalServerErrorException extends CustomException{
    public InternalServerErrorException(ErrorCode errorCode) {
        super(errorCode);
    }
}
