package kr.co.sugarmanager.alarmchallenge.challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


public class RemindChallengeDTO {
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response{
        @Builder.Default
        private List<UserInfoDTO> userInfos = new ArrayList<>();
    }
}
