package kr.co.sugarmanager.business.menu.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import kr.co.sugarmanager.business.global.dto.ErrorResponse;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

public class MenuSaveDTO {
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class Request {
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        private LocalDateTime registedAt;

        private List<FoodDTO> foods;
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
