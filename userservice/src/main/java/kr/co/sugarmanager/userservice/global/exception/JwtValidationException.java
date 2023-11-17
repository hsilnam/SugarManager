package kr.co.sugarmanager.userservice.global.exception;

public class JwtValidationException extends CustomJwtException {
    public JwtValidationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
