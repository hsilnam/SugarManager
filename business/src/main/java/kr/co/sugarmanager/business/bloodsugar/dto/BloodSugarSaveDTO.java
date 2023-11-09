package kr.co.sugarmanager.business.bloodsugar.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import kr.co.sugarmanager.business.global.dto.ErrorResponse;
import lombok.*;

public class BloodSugarSaveDTO {
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class Request {
        @JsonValue
        private BLOODSUGARCATEGORY category;
        private int level;
        private String content;
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
