package kr.co.sugarmanager.userservice.global.util;

import kr.co.sugarmanager.userservice.global.exception.CustomException;
import kr.co.sugarmanager.userservice.global.exception.ErrorCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class APIUtils {

    public static <T> ResponseEntity<ApiResult<T>> result(boolean success, T response, HttpStatus status) {
        return new ResponseEntity<>(new ApiResult<>(success, response, null), status);
    }

    public static ResponseEntity<ApiResult<ApiError>> error(CustomException exception) {
        return new ResponseEntity<>(
                new ApiResult<>(false, null, new ApiError(exception))
                , exception.getErrorCode().getStatus());
    }

    @Getter
    @Setter
    public static class ApiResult<T> {
        private boolean success;
        private T response;
        private ApiError error;

        public ApiResult(boolean success, T response, ApiError error) {
            this.success = success;
            this.response = response;
            this.error = error;
        }

        public ApiResult() {
        }
    }

    @Getter
    @Setter
    public static class ApiError {
        private String code;
        private String message;

        public ApiError(String code, String message) {
            this.code = code;
            this.message = message;
        }

        public ApiError() {
        }

        public ApiError(ErrorCode code) {
            this(code.getCode(), code.getMessage());
        }

        public ApiError(CustomException exception) {
            this(exception.getErrorCode());
        }
    }
}
