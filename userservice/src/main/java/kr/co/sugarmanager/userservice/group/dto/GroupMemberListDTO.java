package kr.co.sugarmanager.userservice.group.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

public class GroupMemberListDTO {
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
        List<UserInfo> users = new ArrayList<>();
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class UserInfo {
        private long uid;
        private String nickname;
        private String profileUrl;
    }
}