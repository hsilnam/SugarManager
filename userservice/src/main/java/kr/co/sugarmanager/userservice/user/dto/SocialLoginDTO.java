package kr.co.sugarmanager.userservice.user.dto;

import lombok.*;

public class SocialLoginDTO {
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Request {
        private String accessToken;
        private String fcmToken;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Response {
        private String accessToken;
        private String refreshToken;
    }
}
