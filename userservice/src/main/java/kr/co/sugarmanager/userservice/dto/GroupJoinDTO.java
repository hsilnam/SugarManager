package kr.co.sugarmanager.userservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

public class GroupJoinDTO {
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Request {
        @JsonIgnore
        private long userPk;

        @JsonProperty("group_code")
        private String groupCode;
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