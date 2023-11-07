package kr.co.sugarmanager.business.challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


public class TodayChallengesDTO {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response{
        @Builder.Default
        private List<UserChallengeInfoDTO> userInfos = new ArrayList<>();
    }
}
