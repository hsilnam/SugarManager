package kr.co.sugarmanager.userservice.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

public class JoinDTO {
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Request {
        private String id;
        private String pw;
        private String email;
        private String name;
        private String nickname;
        private String fcmToken;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Response {
        @JsonIgnore
        private boolean success;

        private String id;
    }
}