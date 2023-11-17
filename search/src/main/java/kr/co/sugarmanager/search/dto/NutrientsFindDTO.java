package kr.co.sugarmanager.search.dto;

import kr.co.sugarmanager.search.entity.NutrientsEntity;
import kr.co.sugarmanager.search.exception.dto.ErrorResponse;
import lombok.*;

import java.util.List;

public class NutrientsFindDTO {
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class Response {
        private boolean success;
        private List<NutrientsEntity> response;
        private ErrorResponse error;
    }
}
