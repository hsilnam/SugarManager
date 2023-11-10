package kr.co.sugarmanager.business.timeline.dto;

import kr.co.sugarmanager.business.global.dto.ErrorResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class TimelineDateDTO {
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response{
        private boolean success;
        private List<Info> response;
        private ErrorResponse error;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Info{
        private Integer hour;
        private Integer minute;
        private Integer second;
        private InfoTypeEnum category;
        private String content;
        private boolean complete;
    }


}