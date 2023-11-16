package kr.co.sugarmanager.business.bloodsugar.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import kr.co.sugarmanager.business.global.dto.ErrorResponse;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

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

        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        private LocalDateTime registedAt;
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
