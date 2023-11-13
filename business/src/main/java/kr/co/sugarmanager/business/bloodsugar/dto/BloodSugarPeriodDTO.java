package kr.co.sugarmanager.business.bloodsugar.dto;

import kr.co.sugarmanager.business.global.dto.ErrorResponse;
import lombok.*;

import java.util.List;

public class BloodSugarPeriodDTO {
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class Response {
        private boolean success;
        private List<BloodSugarPeriodInterface> response;
        private ErrorResponse error;
    }
}