package kr.co.gateway.exception;

public class JwtExpiredException extends CustomJwtException {
    public JwtExpiredException(ErrorCode errorCode) {
        super(errorCode);
    }
}
