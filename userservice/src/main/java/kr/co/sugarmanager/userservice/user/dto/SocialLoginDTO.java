package kr.co.sugarmanager.userservice.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

public class SocialLoginDTO {
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Request {
        @JsonProperty("access_token")
        private String accessToken;

        @JsonProperty("fcm_token")
        private String fcmToken;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Response {
        @JsonProperty("access_token")
        private String accessToken;
        @JsonProperty("refresh_token")
        private String refreshToken;
    }
}
