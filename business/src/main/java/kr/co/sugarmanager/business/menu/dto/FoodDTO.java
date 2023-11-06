package kr.co.sugarmanager.business.menu.dto;

import lombok.*;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FoodDTO {
    private String foodName;
    private float foodCarbohydrate;
    private float foodProtein;
    private float foodDietaryFiber;
    private float foodVitamin;
    private float foodMineral;
    private float foodSalt;
    private float foodSugars;
    private float foodCal;
    private float foodGrams;
}