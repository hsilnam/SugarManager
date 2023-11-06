package kr.co.sugarmanager.userservice.exception;

import static kr.co.sugarmanager.userservice.util.APIUtils.*;

import kr.co.sugarmanager.userservice.util.APIUtils;
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
            log.error("[Server Error]", exception);
        }
        return APIUtils.error(exception);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResult<ApiError>> otherExceptionHandler(Exception exception) {
        if (log.isErrorEnabled()) {
            log.error("[Server Error]", exception.getCause());
        }
        return APIUtils.error(new CustomException(ErrorCode.INTERNAL_SERVER_ERROR_EXCEPTION));
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResult<ApiError>> customErrorExceptionHandler(CustomException exception) {
        return APIUtils.error(exception);
    }
}
