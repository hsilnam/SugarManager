package kr.co.sugarmanager.business.challenge.dto;

import kr.co.sugarmanager.business.global.dto.ErrorResponse;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


public class TodayChallengesDTO {
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response{
        private boolean success;
        @Builder.Default
        private List<UserChallengeInfoDTO> userInfos = new ArrayList<>();
        private String response;
        private ErrorResponse error;
    }
}
