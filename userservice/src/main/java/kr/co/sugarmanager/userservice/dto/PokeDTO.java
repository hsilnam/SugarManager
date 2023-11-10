package kr.co.sugarmanager.userservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import kr.co.sugarmanager.userservice.entity.PokeType;
import lombok.*;

public class PokeDTO {
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Request {
        @JsonIgnore
        private long userPk;

        private String nickname;
        private Long challengeId;
        private PokeType type;//challenge or bloodsugar
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Response {
        @JsonIgnore
        private boolean success;
        private String nickname;
        @JsonSerialize
        private PokeType type;
        private Long challengeId;
    }
}