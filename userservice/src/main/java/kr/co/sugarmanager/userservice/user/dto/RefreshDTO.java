package kr.co.sugarmanager.userservice.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

public class RefreshDTO {
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Request {
        @JsonProperty("refresh_token")
        private String refreshToken;
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Response {
        @JsonProperty("access_token")
        private String accessToken;
    }
}