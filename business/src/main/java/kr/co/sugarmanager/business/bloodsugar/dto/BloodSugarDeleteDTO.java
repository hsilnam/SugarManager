package kr.co.sugarmanager.business.bloodsugar.dto;

import kr.co.sugarmanager.business.global.dto.ErrorResponse;
import lombok.*;

public class BloodSugarDeleteDTO {
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class Request {
        private Long bloodSugarPk;
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class Response {
        private boolean success;
        private String response;
        private ErrorResponse error;
    }
}
