package kr.co.sugarmanager.userservice.global.exception;

public class JwtExpiredException extends CustomJwtException {
    public JwtExpiredException(ErrorCode errorCode) {
        super(errorCode);
    }
}
