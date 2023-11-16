package kr.co.sugarmanager.business.bloodsugar.dto;

import kr.co.sugarmanager.business.global.dto.ErrorResponse;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class BloodSugarSelectDTO {
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class Response {
        private boolean success;
        private ReturnResponse response;
        private ErrorResponse error;
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class ReturnResponse {
        private int bloodSugarMin;
        private int bloodSugarMax;
        private ArrayList<EntityResponse> list;

        public void addList(EntityResponse entityResponse) {
            this.list.add(entityResponse);
        }
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class EntityResponse {
        private Long bloodSugarPk;
        private String category;
        private int level;
        private String content;
        private LocalDateTime createdAt;
        private SUGARBLOODSTATUS status;
    }
}
