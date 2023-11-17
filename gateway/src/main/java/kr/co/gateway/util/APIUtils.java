package kr.co.gateway.util;

import kr.co.gateway.exception.CustomException;
import kr.co.gateway.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

public class APIUtils {

    public static <T> ApiResult<T> result(boolean success, T response, HttpStatus status) {
        return new ApiResult<>(success, response, null);
    }

    public static ApiResult<ApiError> error(CustomException exception) {
        return new ApiResult<>(false, null, new ApiError(exception));
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

        public ApiError(ErrorCode code) {
            this(code.getCode(), code.getMessage());
        }

        public ApiError(CustomException exception) {
            this(exception.getErrorCode());
        }
    }
}
