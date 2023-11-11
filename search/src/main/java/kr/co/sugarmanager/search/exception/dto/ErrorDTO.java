package kr.co.sugarmanager.search.exception.dto;

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
