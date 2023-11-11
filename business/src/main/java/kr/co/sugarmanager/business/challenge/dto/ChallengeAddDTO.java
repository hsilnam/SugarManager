package kr.co.sugarmanager.business.challenge.dto;

import kr.co.sugarmanager.business.global.dto.ErrorResponse;
import lombok.*;

import java.util.List;

public class ChallengeAddDTO {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request{
        private long userPk;
        private String title;
        private int goal;
        private ChallengeTypeEnum type; // enum type
        private boolean alert;
        private Integer hour;
        private Integer minute;
        private List<String> days;
    }

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response{
        private boolean success;
        private String response;
        private ErrorResponse error;
    }


}
