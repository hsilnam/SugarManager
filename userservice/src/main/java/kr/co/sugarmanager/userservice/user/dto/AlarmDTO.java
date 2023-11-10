package kr.co.sugarmanager.userservice.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kr.co.sugarmanager.userservice.user.vo.AlertType;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

public class AlarmDTO {
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Request {
        @JsonIgnore
        private long userPk;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Response {
        @JsonIgnore
        private boolean success;

        @Builder.Default
        private List<AlarmInfo> alarms = new ArrayList<>();
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class AlarmInfo {
        private AlertType category;
        private boolean status;
    }
}