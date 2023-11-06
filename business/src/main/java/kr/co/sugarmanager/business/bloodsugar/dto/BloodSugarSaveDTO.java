package kr.co.sugarmanager.business.bloodsugar.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import kr.co.sugarmanager.business.bloodsugar.entity.BLOODSUGARCATEGORY;
import kr.co.sugarmanager.business.global.dto.ErrorResponse;
import lombok.*;

public class BloodSugarSaveDTO {
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class Request {
        private BLOODSUGARCATEGORY cateory;
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
