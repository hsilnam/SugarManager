package kr.co.sugarmanager.userservice.exception;

import kr.co.sugarmanager.userservice.util.APIUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<APIUtils.ApiResult<APIUtils.ApiError>> customErrorExceptionHandler(CustomException exception) {
        return APIUtils.error(exception);
    }
}
