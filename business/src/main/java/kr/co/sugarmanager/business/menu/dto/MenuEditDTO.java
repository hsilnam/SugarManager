package kr.co.sugarmanager.business.menu.dto;

import kr.co.sugarmanager.business.global.dto.ErrorResponse;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class MenuEditDTO {
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @ToString
    public static class Request {
        private Long userPk;

        private Long menuPk;

        private List<MultipartFile> createdMenuImages;
        private List<Long> deletedMenuImagePks;

        private List<CreatedFood> createdFoods;
        private List<UpdatedFood> updatedFoods;
        private List<Long> deletedFoodPks;
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @ToString
    public static class CreatedFood {
        private String foodName;
        private Float foodCal;
        private Float foodGrams;
        private Float foodCarbohydrate;
        private Float foodProtein;
        private Float foodDietaryFiber;
        private Float foodVitamin;
        private Float foodFat;
        private Float foodSalt;
        private Float foodSugars;
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @ToString
    public static class UpdatedFood {
        private Long foodPk;
        private String foodName;
        private Float foodCal;
        private Float foodGrams;
        private Float foodCarbohydrate;
        private Float foodProtein;
        private Float foodDietaryFiber;
        private Float foodVitamin;
        private Float foodFat;
        private Float foodSalt;
        private Float foodSugars;
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @ToString
    public static class Response {
        private boolean success;
        private String response;
        private ErrorResponse error;
    }
}