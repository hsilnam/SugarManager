package kr.co.sugarmanager.userservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kr.co.sugarmanager.userservice.entity.AlertType;
import lombok.*;

public class AlarmUpdateDTO {
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Request {
        @JsonIgnore
        private long userPk;
        private AlertType category;
        private boolean status;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Response {
        @JsonIgnore
        private boolean success;
    }
}