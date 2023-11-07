package kr.co.sugarmanager.userservice.exception;

import kr.co.sugarmanager.userservice.entity.AlertType;
import kr.co.sugarmanager.userservice.entity.UserInfoValidation;
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
    JSON_BINDING_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "S002", "서버 에러입니다."),
    SQL_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "S003", "서버 에러입니다."),

    //유효성 관련
    NICKNAME_NOT_VALID_EXCEPTION(HttpStatus.BAD_REQUEST, "UV_001", UserInfoValidation.NICKNAME),
    NAME_NOT_VALID_EXCEPTION(HttpStatus.BAD_REQUEST, "UV_002", UserInfoValidation.NAME),
    HEIGHT_NOT_VALID_EXCEPTION(HttpStatus.BAD_REQUEST, "UV_003", UserInfoValidation.HEIGHT),
    WEIGHT_NOT_VALID_EXCEPTION(HttpStatus.BAD_REQUEST, "UV_004", UserInfoValidation.WEIGHT),
    GENDER_NOT_VALID_EXCEPTION(HttpStatus.BAD_REQUEST, "UV_005", UserInfoValidation.GENDER),
    NICKNAME_DUPLICATED_EXCEPTION(HttpStatus.OK, "UV_006", "중복된 닉네임입니다."),
    CATEGORY_NOT_VALID_EXCEPTION(HttpStatus.BAD_REQUEST, "UV_007", AlertType.getDomain());
    private HttpStatus status;
    private String code;
    private String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    ErrorCode(HttpStatus status, String code, UserInfoValidation validation) {
        this.status = status;
        this.code = code;
        this.message = validation.getMessage();
    }
}
