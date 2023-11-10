package kr.co.sugarmanager.business.menu.dto;

import kr.co.sugarmanager.business.global.dto.ErrorResponse;
import lombok.*;

import java.util.ArrayList;

public class MenuSelectDTO {
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class Request {
        private Long userPk;
        private Long menuPk;
    }

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
        private Long menuPk;
        private ArrayList<MenuImage> menuImages;
        private BloodSugar bloodSugar;
        private ArrayList<Food> foods;
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class MenuImage {
        private Long menuImagePk;
        private String menuImageUrl;
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class BloodSugar {
        private int beforeLevel;
        private int afterLevel;
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class Food {
        private Long foodPk;
        private String foodName;
        private float foodCal;
        private float foodGrams;
        private float foodCarbohydrate;
        private float foodProtein;
        private float foodDietaryFiber;
        private float foodVitamin;
        private float foodMineral;
        private float foodSalt;
        private float foodSugars;
    }
}
