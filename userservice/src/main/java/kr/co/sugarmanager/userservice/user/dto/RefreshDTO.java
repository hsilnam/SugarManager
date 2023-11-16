package kr.co.sugarmanager.userservice.user.dto;

import lombok.*;

public class RefreshDTO {
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Request {
        private String refreshToken;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Response {
        private String accessToken;
    }
}