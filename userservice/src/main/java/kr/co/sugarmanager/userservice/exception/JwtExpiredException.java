package kr.co.sugarmanager.userservice.exception;

public class JwtExpiredException extends CustomJwtException {
    public JwtExpiredException(ErrorCode errorCode) {
        super(errorCode);
    }
}
