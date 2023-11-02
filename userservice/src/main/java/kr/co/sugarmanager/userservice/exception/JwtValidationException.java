package kr.co.sugarmanager.userservice.exception;

public class JwtValidationException extends CustomJwtException {
    public JwtValidationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
