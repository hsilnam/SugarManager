package kr.co.sugarmanager.userservice.group.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

public class GroupCreateDTO {
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

        private String groupCode;
    }
}