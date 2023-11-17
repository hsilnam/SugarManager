package kr.co.sugarmanager.business.menu.dto;

public interface MenuPeriodInterface {
    Double getDayFoodCal();
    Double getDayFoodSugars();
    Double getDayFoodProtein();
    Double getDayFoodCarbohydrate();
    Double getDayFoodFat();
    String getTime();
}