package kr.co.sugarmanager.search.exception.dto;

import kr.co.sugarmanager.search.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class ErrorResponse{

    private boolean success;
    private String response;
    private ErrorDTO error;

    public ErrorResponse(String code, String defaultMessage) {
        this.success = false;
        error = new ErrorDTO(code, defaultMessage);
    }
    public static ErrorResponse of (ErrorCode code) {
        return new ErrorResponse(false, null, new ErrorDTO(code.getCode(), code.getMessage()));
    }
}
