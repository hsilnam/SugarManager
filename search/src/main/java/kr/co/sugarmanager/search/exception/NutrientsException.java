package kr.co.sugarmanager.search.exception;

public class NutrientsException extends CustomException {
    public NutrientsException(ErrorCode errorCode) {
        super(errorCode);
    }
}