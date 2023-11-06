package kr.co.gateway.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    //유저
    UNAUTHORIZATION_EXCEPTION(HttpStatus.UNAUTHORIZED, "U001", "인증에 실패했습니다."),
    FORBIDDEN_EXCEPTION(HttpStatus.FORBIDDEN, "U002", "권한이 없습니다."),
    JWT_EXPIRED_EXCEPTION(HttpStatus.UNAUTHORIZED, "U003", "토큰 시간이 만료되었습니다."),
    JWT_BADREQUEST_EXCEPTION(HttpStatus.BAD_REQUEST, "U004", "토큰 정보가 잘못되었습니다."),
    USER_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "U005", "유저가 존재하지 않습니다."),

    //서버관련
    INTERNAL_SERVER_ERROR_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "S001", "서버에러입니다."),
    JSON_BINDING_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "S002", "서버 에러입니다.");

    private HttpStatus status;
    private String code;
    private String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
