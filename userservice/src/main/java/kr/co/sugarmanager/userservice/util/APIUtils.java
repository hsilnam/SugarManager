package kr.co.sugarmanager.userservice.util;

import kr.co.sugarmanager.userservice.exception.CustomException;
import kr.co.sugarmanager.userservice.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class APIUtils {

    public static <T> ResponseEntity<ApiResult<T>> result(boolean success, T response, HttpStatus status) {
        return new ResponseEntity<>(new ApiResult<>(success,response,null),status);
    }

    public static ResponseEntity<ApiError> error(CustomException exception){
        return new ResponseEntity(new ApiError(exception),exception.getErrorCode().getStatus());
    }

    @Getter
    public static class ApiResult<T> {
        private final boolean success;
        private final T response;
        private final ApiError error;

        public ApiResult(boolean success, T response, ApiError error) {
            this.success = success;
            this.response = response;
            this.error = error;
        }
    }

    @Getter
    public static class ApiError {
        private final String code;
        private final String message;

        public ApiError(String code, String message) {
            this.code = code;
            this.message = message;
        }

        public ApiError(ErrorCode code){
            this(code.getCode(),code.getMessage());
        }

        public ApiError(CustomException exception){
            this(exception.getErrorCode());
        }
    }
}
