package kr.co.sugarmanager.userservice.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

public class LoginDTO {
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Request {
        private String id;
        private String pw;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Response {
        @JsonIgnore
        private boolean success;
        private String accessToken;
        private String refreshToken;
    }
}