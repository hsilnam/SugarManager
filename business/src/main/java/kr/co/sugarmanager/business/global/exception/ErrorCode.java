package kr.co.sugarmanager.business.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "잘못된 입력 값입니다."),
    MISSING_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C002", "인자가 부족합니다."),
    HANDLE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "C003", "접근권한이 없습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C004", "사용할 수 없는 메서드입니다."),
    NOT_EXIST_API(HttpStatus.BAD_REQUEST, "C005", "요청 주소가 올바르지 않습니다."),
    INVALID_PATH_VALUE(HttpStatus.BAD_REQUEST,"C006","요청이 잘못됐습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C007", "서버 에러"),

    // 식단 등록
    INVALID_IMAGE_TYPE_ERROR(HttpStatus.BAD_REQUEST, "M001", "이미지가 형식이 잘못되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

}
