package kr.co.sugarmanager.business.challenge.dto;

import kr.co.sugarmanager.business.global.dto.ErrorResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

public class ChallengeClaimDTO {
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response{
        private boolean success;
        private String response;
        private ErrorResponse error;
    }
}
