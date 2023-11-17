package kr.co.gateway.exception;

public class JwtValidationException extends CustomJwtException {
    public JwtValidationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
