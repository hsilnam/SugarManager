package kr.co.sugarmanager.business.global.dto;

import kr.co.sugarmanager.business.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class ErrorDTO {
    private String code;
    private String message;
}
