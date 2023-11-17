package kr.co.sugarmanager.business.challenge.dto;

import kr.co.sugarmanager.business.global.dto.ErrorResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

public class UserChallengeAllDTO {
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response{
        private boolean success;
        private InfoResponse response;
        private ErrorResponse error;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InfoResponse{
        private boolean pokeAbled;
        @Builder.Default
        private List<Info> list = new ArrayList<>();
    }
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Info{
        private long challengePk;
        private ChallengeTypeEnum type;
        private String title;
        private int count;
        private int goal;
        private boolean alert;
        private Integer hour;
        private Integer minute;
        private List<String> days;
    }

}
