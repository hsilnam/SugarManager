package kr.co.sugarmanager.business.menu.dto;

import kr.co.sugarmanager.business.global.dto.ErrorResponse;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class MenuDayDTO {
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
        private Double daySugars;
        private ArrayList<MenuPreview> menuPreviews;

        public void addList(MenuPreview entityResponse) {
            this.menuPreviews.add(entityResponse);
        }
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class MenuPreview {
        private Long menuPk;
        private Float foodCal;
        private Float foodSugars;
        private Float foodProtein;
        private Float foodCarbohydrate;
        private Float foodFat;
        private LocalDateTime registedAt;
    }
}
