package kr.co.sugarmanager.business.menu.dto;

import kr.co.sugarmanager.business.bloodsugar.dto.BloodSugarPeriodInterface;
import kr.co.sugarmanager.business.global.dto.ErrorResponse;
import lombok.*;

import java.util.List;

public class MenuPeriodDTO {
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class Response {
        private boolean success;
        private List<MenuPeriodInterface> response;
        private ErrorResponse error;
    }
//    public static class DayMenuPreview {
//        private Double dayFoodCal;
//        private Double dayFoodSugars;
//        private Double dayFoodProtein;
//        private Double dayFoodCarbohydrate;
//        private Double dayFoodFat;
//        private String time;
//    }
}