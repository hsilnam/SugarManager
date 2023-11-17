package kr.co.sugarmanager.userservice.global.exception;

import static kr.co.sugarmanager.userservice.global.util.APIUtils.*;

import kr.co.sugarmanager.userservice.global.util.APIUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<ApiResult<ApiError>> internalServerErrorExceptionHandler(InternalServerErrorException exception) {
        if (log.isErrorEnabled()) {
            log.error("[Custom Server Error]", exception);
            log.info("[Custom Server Error] {},{}", exception, exception.getErrorCode().getMessage());
        }
        return APIUtils.error(exception);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResult<ApiError>> otherExceptionHandler(Exception exception) {
        if (log.isErrorEnabled()) {
            log.error("[Etc Server Error]", exception.getCause());
            log.info("[Etc Server Error] {},{}", exception.getCause(), exception.getMessage());
        }
        return APIUtils.error(new CustomException(ErrorCode.INTERNAL_SERVER_ERROR_EXCEPTION));
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResult<ApiError>> customErrorExceptionHandler(CustomException exception) {
        return APIUtils.error(exception);
    }
}
