package kr.co.sugarmanager.userservice.exception;

public class InternalServerErrorException extends CustomException{
    public InternalServerErrorException(ErrorCode errorCode) {
        super(errorCode);
    }
}
