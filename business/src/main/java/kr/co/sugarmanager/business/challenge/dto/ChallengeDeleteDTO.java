package kr.co.sugarmanager.business.challenge.dto;

import kr.co.sugarmanager.business.global.dto.ErrorResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ChallengeDeleteDTO {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request{
        private long challengePk;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response{
        private boolean success;
        private String response;
        private ErrorResponse error;
    }
}
