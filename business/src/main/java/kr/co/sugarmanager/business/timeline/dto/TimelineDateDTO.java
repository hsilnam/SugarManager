package kr.co.sugarmanager.business.timeline.dto;

import kr.co.sugarmanager.business.global.dto.ErrorResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

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
    public static class Info implements Comparable<Info>{
        private Integer hour;
        private Integer minute;
        private Integer second;
        private InfoTypeEnum category;
        private String content;
        private boolean complete;

        @Override
        public int compareTo(Info i){
            if (!Objects.equals(this.hour, i.hour)){
                return Integer.compare(this.hour, i.hour);
            }
            else {
                if (!Objects.equals(this.minute, i.minute)){
                    return Integer.compare(this.minute, i.minute);
                }
                else {
                    return Integer.compare(this.second, i.second);
                }
            }
        }
    }
}